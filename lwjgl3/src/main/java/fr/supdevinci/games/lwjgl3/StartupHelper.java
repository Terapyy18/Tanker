/*
 * Copyright 2020 damios
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at:
 * https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
// Note : la licence et le copyright ci-dessus s'appliquent uniquement à ce fichier.
package fr.supdevinci.games.lwjgl3;

import com.badlogic.gdx.Version;
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3NativesLoader;

import org.lwjgl.system.JNI;
import org.lwjgl.system.linux.UNISTD;
import org.lwjgl.system.macosx.LibC;
import org.lwjgl.system.macosx.ObjCRuntime;

import java.io.File;
import java.lang.management.ManagementFactory;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * Un objet d'aide au démarrage du jeu, proposant trois utilitaires liés à LWJGL3 sur divers systèmes d'exploitation.
 * <p>
 * Les utilitaires sont les suivants :
 * <ul>
 *  <li> Windows : Empêche un plantage courant lié à l'extraction des fichiers de bibliothèque partagée de LWJGL3.</li>
 *  <li> macOS : Lance un processus JVM enfant avec {@code -XstartOnFirstThread} dans les arguments de la JVM (si ce n'était pas déjà fait).
 *  Ceci est requis pour que LWJGL3 fonctionne sur macOS.</li>
 *  <li> Linux (GPU NVIDIA uniquement) : Lance un processus JVM enfant avec la variable d'environnement {@code __GL_THREADED_OPTIMIZATIONS}
 *  {@link System#getenv(String)} fixée à {@code 0} (si ce n'était pas déjà le cas). Ceci est requis pour que
 *  LWJGL3 fonctionne sur Linux avec des GPU NVIDIA.</li>
 * </ul>
 * <a href="https://jvm-gaming.org/t/starting-jvm-on-mac-with-xstartonfirstthread-programmatically/57547">Basé sur ce post java-gaming.org par kappa</a>
 * @author damios
 */
public class StartupHelper {

	private StartupHelper() {}

	private static final String JVM_RESTARTED_ARG = "jvmIsRestarted";

	/**
	 * Doit être appelé uniquement sur Linux. Vérifiez l'OS d'abord !
	 * @return si des pilotes NVIDIA sont présents sur Linux.
	 */
	public static boolean isLinuxNvidia() {
		String[] drivers = new File("/proc/driver").list(
			(dir, path) -> path.toUpperCase(Locale.ROOT).contains("NVIDIA")
		);
		if (drivers == null) return false;
		return drivers.length > 0;
	}

	/**
	 * Applique les utilitaires comme décrit par le Javadoc de {@link StartupHelper}.
	 * <p>
	 * Toutes les {@link System#getenv() variables d'environnement} sont copiées dans le processus JVM enfant (s'il est lancé), comme
	 * spécifié par {@link ProcessBuilder#environment()} ; il en va de même pour les
	 * {@link System#getProperties() propriétés système}.
	 * <p>
	 * <b>Utilisation :</b>
	 * <pre><code>
	 * public static void main(String[] args) {
	 * 	 if (StartupHelper.startNewJvmIfRequired()) return;
	 * 	 // ... Le reste du main() va ici, comme d'habitude.
	 * }
	 * </code></pre>
	 * @return si un processus JVM enfant a été lancé ou non.
	 */
	public static boolean startNewJvmIfRequired() {
		return startNewJvmIfRequired(true);
	}

	/**
	 * Applique les utilitaires comme décrit par le Javadoc de {@link StartupHelper}.
	 * <p>
	 * Toutes les {@link System#getenv() variables d'environnement} sont copiées dans le processus JVM enfant (s'il est lancé), comme
	 * spécifié par {@link ProcessBuilder#environment()} ; il en va de même pour les
	 * {@link System#getProperties() propriétés système}.
	 * <p>
	 * <b>Utilisation :</b>
	 * <pre><code>
	 * public static void main(String[] args) {
	 *   // Le paramètre sur la ligne suivante pourrait être false si vous ne voulez pas hériter des E/S.
	 * 	 if (StartupHelper.startNewJvmIfRequired(true)) return;
	 * 	 // ... Le reste du main() va ici, comme d'habitude.
	 * }
	 * </code></pre>
	 * @param inheritIO si les entrées/sorties doivent être héritées dans le processus JVM enfant. Veuillez noter que l'activation de ceci
	 *                  bloquera le thread jusqu'à ce que le processus JVM enfant s'arrête de s'exécuter.
	 * @return si un processus JVM enfant a été lancé ou non.
	 */
	public static boolean startNewJvmIfRequired(boolean inheritIO) {
		String osName = System.getProperty("os.name").toLowerCase(Locale.ROOT);
		if (osName.contains("mac")) return startNewJvm0(/*isMac =*/ true, inheritIO);
		if (osName.contains("windows")) {
			// Ici, nous essayons de contourner un problème lié à la façon dont LWJGL3 charge ses fichiers .dll extraits.
			// Par défaut, LWJGL3 extrait dans le répertoire spécifié par "java.io.tmpdir" : généralement, le dossier personnel de l'utilisateur.
			// Si le nom de l'utilisateur contient des caractères non-ASCII (ou certains non-alphanumériques), cela échouerait.
			// En extrayant dans le dossier "ProgramData" concerné, qui est généralement "C:\ProgramData", nous évitons cela.
			// Nous changeons également temporairement la propriété "user.name" pour une propriété sans caractères invalides.
			// Nous rétablissons nos changements immédiatement après le chargement des natifs LWJGL3.
			String programData = System.getenv("ProgramData");
			if (programData == null) programData = "C:\\Temp"; // si ProgramData n'est pas défini, on tente une solution de repli.
			String prevTmpDir = System.getProperty("java.io.tmpdir", programData);
			String prevUser = System.getProperty("user.name", "libGDX_User");
			System.setProperty("java.io.tmpdir", programData + "\\libGDX-temp");
			System.setProperty(
				"user.name",
				("User_" + prevUser.hashCode() + "_GDX" + Version.VERSION).replace('.', '_')
			);
			Lwjgl3NativesLoader.load();
			System.setProperty("java.io.tmpdir", prevTmpDir);
			System.setProperty("user.name", prevUser);
			return false;
		}
		return startNewJvm0(/*isMac =*/ false, inheritIO);
	}

	private static final String MAC_JRE_ERR_MSG = "Une installation Java n'a pas pu être trouvée. Si vous distribuez cette application avec un JRE intégré, assurez-vous de définir l'argument '-XstartOnFirstThread' manuellement !";
	private static final String LINUX_JRE_ERR_MSG = "Une installation Java n'a pas pu être trouvée. Si vous distribuez cette application avec un JRE intégré, assurez-vous de définir la variable d'environnement '__GL_THREADED_OPTIMIZATIONS' à '0' !";
	private static final String CHILD_LOOP_ERR_MSG = "Le processus JVM actuel est déjà un processus enfant, mais StartupHelper a tenté d'en lancer un autre ! C'est un état anormal qui ne devrait pas arriver ! Votre jeu peut planter ou ne pas fonctionner correctement !";

	/**
	 * Lance un processus JVM enfant si sur macOS, ou sur Linux avec des pilotes NVIDIA.
	 * <p>
	 * Toutes les {@link System#getenv() variables d'environnement} sont copiées dans le processus JVM enfant (s'il est lancé), comme
	 * spécifié par {@link ProcessBuilder#environment()} ; il en va de même pour les
	 * {@link System#getProperties() propriétés système}.
	 * @param isMac si l'OS actuel est macOS. Si c'est `false`, on suppose que l'OS actuel est Linux (et
	 *             une vérification immédiate des pilotes NVIDIA est effectuée).
	 * @param inheritIO si les entrées/sorties doivent être héritées dans le processus JVM enfant. Veuillez noter que l'activation de ceci
	 *                 bloquera le thread jusqu'à ce que le processus JVM enfant s'arrête de s'exécuter.
	 * @return si un processus JVM enfant a été lancé ou non.
	 */
	public static boolean startNewJvm0(boolean isMac, boolean inheritIO) {
		long processID = getProcessID(isMac);
		if (!isMac) {
			// Pas besoin de redémarrer pour Linux non-NVIDIA
			if (!isLinuxNvidia()) return false;
			// vérifier si __GL_THREADED_OPTIMIZATIONS est déjà désactivé
			if ("0".equals(System.getenv("__GL_THREADED_OPTIMIZATIONS"))) return false;
		} else {
			// Pas besoin de -XstartOnFirstThread sur une image native Graal
			if (!System.getProperty("org.graalvm.nativeimage.imagecode", "").isEmpty()) return false;

			// Vérifie si nous sommes déjà sur le thread principal, par exemple via Construo.
			long objcMsgSend = ObjCRuntime.getLibrary().getFunctionAddress("objc_msgSend");
			long nsThread = ObjCRuntime.objc_getClass("NSThread");
			long currentThread = JNI.invokePPP(nsThread, ObjCRuntime.sel_getUid("currentThread"), objcMsgSend);
			boolean isMainThread = JNI.invokePPZ(currentThread, ObjCRuntime.sel_getUid("isMainThread"), objcMsgSend);
			if (isMainThread) return false;

			if ("1".equals(System.getenv("JAVA_STARTED_ON_FIRST_THREAD_" + processID))) return false;
		}

		// Vérifie si ce processus JVM est déjà un processus JVM enfant.
		// Cet état ne devrait pas être atteignable normalement, mais cela nous empêche de lancer indéfiniment de nouveaux processus enfants.
		if ("true".equals(System.getProperty(JVM_RESTARTED_ARG))) {
			System.err.println(CHILD_LOOP_ERR_MSG);
			return false;
		}

		// Lance le processus JVM enfant avec les variables d'environnement ou arguments JVM mis à jour
		List<String> jvmArgs = new ArrayList<>();
		// La ligne suivante est utilisée en supposant que vous ciblez Java 8, le minimum pour LWJGL3.
		String javaExecPath = System.getProperty("java.home") + "/bin/java";
		// Si vous ciblez Java 9 ou supérieur, vous pourriez utiliser ce qui suit à la place de la ligne ci-dessus :
		//String javaExecPath = ProcessHandle.current().info().command().orElseThrow()
		if (!(new File(javaExecPath).exists())) {
			System.err.println(getJreErrMsg(isMac));
			return false;
		}

		jvmArgs.add(javaExecPath);
		if (isMac) jvmArgs.add("-XstartOnFirstThread");
		jvmArgs.add("-D" + JVM_RESTARTED_ARG + "=true");
		jvmArgs.addAll(ManagementFactory.getRuntimeMXBean().getInputArguments());
		jvmArgs.add("-cp");
		jvmArgs.add(System.getProperty("java.class.path"));
		String mainClass = System.getenv("JAVA_MAIN_CLASS_" + processID);
		if (mainClass == null) {
			StackTraceElement[] trace = Thread.currentThread().getStackTrace();
			if (trace.length > 0) mainClass = trace[trace.length - 1].getClassName();
			else {
				System.err.println("La classe principale n'a pas pu être déterminée.");
				return false;
			}
		}
		jvmArgs.add(mainClass);

		try {
			ProcessBuilder processBuilder = new ProcessBuilder(jvmArgs);
			if (!isMac) processBuilder.environment().put("__GL_THREADED_OPTIMIZATIONS", "0");

			if (!inheritIO) processBuilder.start();
			else processBuilder.inheritIO().start().waitFor();
		} catch (Exception e) {
			System.err.println("Un problème est survenu lors du redémarrage de la JVM.");
			// noinspection CallToPrintStackTrace
			e.printStackTrace();
		}

		return true;
	}

	private static String getJreErrMsg(boolean isMac) {
		if (isMac) return MAC_JRE_ERR_MSG;
		else return LINUX_JRE_ERR_MSG;
	}

	private static long getProcessID(boolean isMac) {
		if (isMac) return LibC.getpid();
		else return UNISTD.getpid();
	}
}