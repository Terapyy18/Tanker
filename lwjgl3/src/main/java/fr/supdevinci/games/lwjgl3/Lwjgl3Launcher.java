package fr.supdevinci.games.lwjgl3;

import com.badlogic.gdx.backends.lwjgl3.Lwjgl3Application;
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3ApplicationConfiguration;
import fr.supdevinci.games.Main;

/** Lance l'application de bureau (LWJGL3). */
public class Lwjgl3Launcher {
    public static void main(String[] args) {
        if (StartupHelper.startNewJvmIfRequired()) return; // Cela gère le support macOS et aide sur Windows.
        createApplication();
    }

    private static Lwjgl3Application createApplication() {
        return new Lwjgl3Application(new Main(), getDefaultConfiguration());
    }

    private static Lwjgl3ApplicationConfiguration getDefaultConfiguration() {
        Lwjgl3ApplicationConfiguration configuration = new Lwjgl3ApplicationConfiguration();
        configuration.setTitle("Tanker");
        //// Le Vsync limite le nombre d'images par seconde à ce que votre matériel peut afficher, et aide à éliminer
        //// le déchirement de l'écran (tearing). Ce paramètre ne fonctionne pas toujours sur Linux, la ligne suivante est donc une protection.
        configuration.useVsync(true);
        //// Limite les FPS au taux de rafraîchissement du moniteur actuellement actif, plus 1 pour essayer de correspondre aux taux de
        //// rafraîchissement fractionnaires. Le réglage Vsync ci-dessus devrait limiter les FPS réels pour correspondre au moniteur.
        configuration.setForegroundFPS(Lwjgl3ApplicationConfiguration.getDisplayMode().refreshRate + 1);
        //// Si vous supprimez la ligne ci-dessus et réglez Vsync sur false, vous pouvez obtenir des FPS illimités, ce qui peut être
        //// utile pour tester les performances, mais peut aussi être très stressant pour certains matériels.
        //// Vous devrez peut-être également configurer les pilotes GPU pour désactiver complètement le Vsync ; cela peut causer du déchirement d'écran.

        configuration.setWindowedMode(1280, 720);
        //// Vous pouvez changer ces fichiers ; ils sont dans lwjgl3/src/main/resources/ .
        //// Ils peuvent également être chargés depuis la racine de assets/ .
        configuration.setWindowIcon("libgdx128.png", "libgdx64.png", "libgdx32.png", "libgdx16.png");

        //// Cela pourrait améliorer la compatibilité avec les machines Windows ayant des pilotes OpenGL défectueux, les Macs
        //// avec Apple Silicon qui doivent de toute façon émuler la compatibilité avec OpenGL, et plus encore.
        //// Cela utilise la dépendance `com.badlogicgames.gdx:gdx-lwjgl3-angle` pour fonctionner.
        //// Vous devriez ajouter cette ligne à lwjgl3/build.gradle , sous la dépendance `gdx-backend-lwjgl3`:
        ////     implementation "com.badlogicgames.gdx:gdx-lwjgl3-angle:$gdxVersion"
        //// Vous pouvez choisir d'ajouter la ligne suivante et la dépendance mentionnée si vous voulez ; elles
        //// ne sont pas destinées aux jeux qui utilisent GL30 (qui est la compatibilité avec OpenGL ES 3.0).
        //// Sachez que cela pourrait ne pas bien fonctionner dans certains cas.
//        configuration.setOpenGLEmulation(Lwjgl3ApplicationConfiguration.GLEmulation.ANGLE_GLES20, 0, 0);

        return configuration;
    }
}