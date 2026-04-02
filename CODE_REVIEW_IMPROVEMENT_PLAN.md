# Plan d'amélioration pour une meilleure review de code

## Objectif

Aligner le projet avec les notions du cours et améliorer la qualité perçue du code sur les axes suivants :

- structuration
- encapsulation
- cycle de vie des objets
- testabilité
- patrons de conception
- inversion de contrôle

## Ce qui est déjà mieux

- La logique principale est maintenant plus séparée via `GameWorld`, `GameRenderer` et `GameInputHandler`.
- `LevelSystem` reste une classe métier simple et testée.
- Le projet a déjà une base ECS / systèmes plus lisible qu'auparavant.

## Priorités

### 1. Corriger les problèmes de cycle de vie

- Ajouter `hide()` ou l'équivalent là où les écrans créent des ressources.
- S'assurer que `MainMenuScreen` libère ses objets graphiques quand l'écran change.
- Éviter les allocations répétées dans `renderPauseMenu()`.
- Centraliser les ressources UI réutilisables au lieu de créer `SpriteBatch`, `ShapeRenderer` et `BitmapFont` à chaque frame.

### 2. Casser les dépendances directes à l'input et aux ressources

- Déplacer la lecture clavier/souris hors de `Tank`.
- Garder la gestion d'entrée dans `GameInputHandler`.
- Éviter que les classes métier chargent directement des textures ou accèdent à `Gdx`.
- Introduire des interfaces ou objets injectés pour ce qui dépend du framework.

### 3. Rendre les systèmes déterministes

- Remplacer `MathUtils.random()` par une source d'aléatoire injectable.
- Permettre de tester `WaveManager` sans dépendre du hasard global.
- Faire de même pour les comportements qui dépendent du temps ou de l'état externe.

### 4. Mieux respecter l'encapsulation

- Éviter d'exposer des listes mutables directement depuis `EntityManager`.
- Préférer des méthodes métier explicites à des accès bruts aux collections internes.
- Limiter les modifications d'état qui contournent les règles métier.

### 5. Clarifier les états du jeu

- Réduire les booléens dispersés quand ils représentent un vrai état métier.
- Vérifier les transitions `pause`, `upgrade`, `victory`, `game over`.
- Corriger les cas où l'UI affiche une action qui n'est pas réellement possible.

### 6. Renforcer les tests

- Ajouter des tests pour `WaveManager`.
- Ajouter des tests pour `CollisionHandler`.
- Ajouter des tests pour `EntityManager` ou la logique d'assemblage du monde.
- Couvrir les transitions d'écran et les cas de pause / victoire / défaite.
- Garder les tests actuels sur `Tank` et `LevelSystem`.

## Ordre recommandé

1. Corriger les problèmes de cycle de vie.
2. Déplacer l'input et le hasard hors des classes métier.
3. Sécuriser l'encapsulation des collections.
4. Ajouter les tests manquants.
5. Refaire une passe de lisibilité et de documentation.

## Critères de meilleure review

Le projet aura une meilleure review si :

- les responsabilités sont plus nettes
- les classes métier sont plus testables
- les dépendances sont plus injectées que construites à la main
- les ressources sont libérées proprement
- les états du jeu sont explicites
- les tests couvrent les règles importantes du gameplay

## Résultat attendu

À la fin de ce plan, on devrait pouvoir dire que le projet :

- correspond mieux aux notions du cours
- présente une architecture plus défendable à l'oral
- limite les bugs de cycle de vie
- est plus simple à faire évoluer et à tester
