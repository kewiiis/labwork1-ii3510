# Documentation Complète - Application SCRUD Students

## 📋 Table des Matières

1. [Vue d'ensemble](#vue-densemble)
2. [Architecture](#architecture)
3. [Flux de Données](#flux-de-données)
4. [Structure du Projet](#structure-du-projet)
5. [Fonctionnalités](#fonctionnalités)
6. [Authentification](#authentification)
7. [Gestion des Rôles](#gestion-des-rôles)
8. [Business Logic](#business-logic)
9. [Navigation](#navigation)
10. [Guide de Démarrage](#guide-de-démarrage)
11. [Dépannage](#dépannage)
12. [Décisions Techniques](#décisions-techniques)

---

## Vue d'ensemble

Cette application Android implémente un système de gestion universitaire avec authentification, gestion des rôles (Étudiant/Enseignant), gestion des cours, inscriptions et notes. Le projet utilise **MVVM + Repository + Room + Hilt + Jetpack Compose** avec Material Design 3.

### Technologies Utilisées

- **Kotlin** : Langage de programmation
- **Jetpack Compose** : Framework UI moderne
- **Material Design 3** : Design system
- **Room** : Base de données locale
- **Hilt** : Injection de dépendances
- **Coroutines & Flow** : Programmation asynchrone réactive
- **Navigation Compose** : Navigation déclarative
- **StateFlow** : Gestion d'état réactive

### Versions

- **compileSdk** : 36
- **targetSdk** : 35
- **minSdk** : 29
- **JDK** : 17 (requis)

---

## Architecture

### Architecture MVVM

L'application suit une architecture **MVVM (Model-View-ViewModel)** avec séparation claire des responsabilités :

```
┌─────────────────────────────────────────────────────────┐
│                    UI Layer (Compose)                     │
│  - Écrans (Screens)                                      │
│  - Composables (Reusable components)                    │
│  - collectAsState() pour observer StateFlow             │
└─────────────────────────────────────────────────────────┘
                         ↕
┌─────────────────────────────────────────────────────────┐
│              Presentation Layer (ViewModels)            │
│  - AuthViewModel, StudentViewModel, etc.                 │
│  - StateFlow pour exposer l'état                        │
│  - viewModelScope.launch pour coroutines                │
│  - Validation et logique métier                        │
└─────────────────────────────────────────────────────────┘
                         ↕
┌─────────────────────────────────────────────────────────┐
│              Repository Layer (Repositories)            │
│  - AuthRepository, StudentRepository, etc.              │
│  - Abstraction de l'accès aux données                  │
│  - Combinaison de sources (DB, SharedPreferences)      │
└─────────────────────────────────────────────────────────┘
                         ↕
┌─────────────────────────────────────────────────────────┐
│              Data Layer (Room + DAOs)                    │
│  - Entités (Entities) : User, Student, Course, etc.     │
│  - DAOs : UserDao, StudentDao, CourseDao, etc.          │
│  - Flow<List<T>> pour réactivité                       │
└─────────────────────────────────────────────────────────┘
                         ↕
┌─────────────────────────────────────────────────────────┐
│              Database Layer (Room)                      │
│  - AppDatabase : Base de données SQLite                │
│  - Version : 2                                         │
└─────────────────────────────────────────────────────────┘
```

### Hilt Dependency Injection

**Hilt** gère l'injection de dépendances :

- **`@HiltAndroidApp`** sur `SCRUDApplication`
- **`@AndroidEntryPoint`** sur `MainActivity`
- **`@HiltViewModel`** sur les ViewModels
- **`@Module @InstallIn(SingletonComponent::class)`** dans `AppModule`
- **`@Provides`** pour les singletons (DB, DAOs, Repositories)

---

## Flux de Données

### Lecture (Read Operations)

```
Room DB (changement)
    ↓
DAO Flow<UserEntity?>
    ↓
Repository.getCurrentUser() : Flow<UserEntity?>
    ↓
ViewModel init : collect → StateFlow
    ↓
UI collectAsState()
    ↓
Recomposition automatique
```

### Écriture (Write Operations)

```
UI Event (Button click)
    ↓
ViewModel function (viewModelScope.launch)
    ↓
Repository suspend function
    ↓
DAO suspend function
    ↓
Room DB (update)
    ↓
Flow émet nouvelle valeur
    ↓
ViewModel StateFlow mis à jour
    ↓
UI collectAsState() → Recomposition
```

### Exemple Concret : Login

1. **UI** : Utilisateur saisit email/password et clique sur "Login"
2. **ViewModel** : `AuthViewModel.login()` est appelé
3. **Coroutine** : `viewModelScope.launch` démarre
4. **Repository** : `AuthRepository.login()` vérifie credentials
5. **DAO** : `UserDao.getUserByEmail()` interroge la DB
6. **Session** : `AuthRepository.saveSession()` sauvegarde dans SharedPreferences
7. **StateFlow** : `_authState.value = AuthState.LoggedIn(user, role)`
8. **UI** : `collectAsState()` détecte le changement → Recomposition → Navigation vers Home

---

## Structure du Projet

### Package Principal

```
com.tumme.scrudstudents/
├── data/
│   ├── local/
│   │   ├── model/          # Entités Room
│   │   │   ├── UserEntity.kt
│   │   │   ├── StudentEntity.kt
│   │   │   ├── TeacherEntity.kt
│   │   │   ├── CourseEntity.kt
│   │   │   ├── SubscribeEntity.kt
│   │   │   ├── Role.kt
│   │   │   └── LevelCourse.kt
│   │   ├── dao/            # Data Access Objects
│   │   │   ├── UserDao.kt
│   │   │   ├── StudentDao.kt
│   │   │   ├── TeacherDao.kt
│   │   │   ├── CourseDao.kt
│   │   │   └── SubscribeDao.kt
│   │   └── AppDatabase.kt  # Base de données Room
│   └── repository/          # Repositories
│       ├── AuthRepository.kt
│       ├── StudentRepository.kt
│       ├── TeacherRepository.kt
│       ├── CourseRepository.kt
│       ├── SubscribeRepository.kt
│       └── SCRUDRepository.kt (legacy)
├── viewmodel/               # ViewModels
│   ├── AuthViewModel.kt
│   ├── StudentViewModel.kt
│   ├── TeacherViewModel.kt
│   ├── CourseViewModel.kt
│   └── SubscribeViewModel.kt
├── ui/
│   ├── auth/                # Écrans d'authentification
│   │   ├── SplashScreen.kt
│   │   ├── LoginScreen.kt
│   │   └── RegisterScreen.kt
│   ├── student/              # Écrans étudiant
│   │   ├── StudentHomeScreen.kt
│   │   ├── StudentCourseListScreen.kt
│   │   ├── StudentSubscribeScreen.kt
│   │   ├── StudentGradesScreen.kt
│   │   └── StudentFinalGradeScreen.kt
│   ├── teacher/              # Écrans enseignant
│   │   ├── TeacherHomeScreen.kt
│   │   ├── TeacherCourseListScreen.kt
│   │   ├── TeacherStudentListScreen.kt
│   │   └── TeacherGradeEntryScreen.kt
│   └── navigation/          # Navigation
│       ├── Routes.kt
│       └── AppNavHost.kt
├── di/                      # Injection de dépendances
│   └── AppModule.kt
└── util/                    # Utilitaires
    └── PasswordHasher.kt
```

---

## Fonctionnalités

### Authentification

- **Login** : Connexion avec email et mot de passe
- **Register** : Inscription avec choix du rôle (Student/Teacher)
- **Session** : Persistance via SharedPreferences
- **Timeout** : Déconnexion automatique après 30 minutes d'inactivité
- **Splash Screen** : Redirection automatique selon l'état de connexion

### Gestion des Étudiants

- **Liste** : Affichage de tous les étudiants
- **Création** : Formulaire d'inscription avec validation
- **Filtrage** : Par niveau d'études (P1, P2, P3, B1, B2, B3, A1, A2, A3, MS, PhD)
- **Profil** : Informations personnelles (nom, prénom, date de naissance, genre)

### Gestion des Cours

- **Liste** : Affichage de tous les cours
- **Création** : Formulaire avec validation :
  - ECTS > 0 (obligatoire)
  - Niveau valide (P1, P2, P3, B1, B2, B3, A1, A2, A3, MS, PhD)
  - Nom requis
- **Association** : Chaque cours est associé à un enseignant

### Gestion des Inscriptions

- **Liste** : Affichage avec noms d'étudiants et cours (JOIN côté UI)
- **Création** : Formulaire avec dropdowns pour sélectionner étudiant et cours
- **Score** : Saisie de note (0..20)
- **Validation** : Score entre 0 et 20, étudiant et cours sélectionnés

### Calcul de Notes Pondérées

- **Formule** : `Σ(score × ECTS) / Σ(ECTS)`
- **Affichage** : Par niveau d'études
- **Gestion division par zéro** : Retourne `null` si aucun ECTS

---

## Authentification

### Hash des Mots de Passe

Les mots de passe sont hashés avec **SHA-256** (via `PasswordHasher.kt`). **Note** : Ceci est uniquement à des fins éducatives. En production, utilisez des algorithmes sécurisés comme bcrypt ou Argon2.

### Gestion de Session

La session est persistée dans **SharedPreferences** avec les clés suivantes :

- `current_user_id` : ID de l'utilisateur connecté
- `current_user_email` : Email de l'utilisateur
- `current_user_role` : Rôle (STUDENT/TEACHER)
- `last_activity_timestamp` : Timestamp de la dernière activité

### Timeout de Session

- **Durée** : 30 minutes d'inactivité
- **Vérification** : Toutes les minutes dans `AuthViewModel`
- **Déconnexion** : Automatique si timeout détecté

### États d'Authentification

```kotlin
sealed class AuthState {
    object Loading : AuthState()
    data class LoggedIn(val user: UserEntity, val role: Role) : AuthState()
    object LoggedOut : AuthState()
    data class Error(val message: String) : AuthState()
}
```

---

## Gestion des Rôles

### Rôles Disponibles

- **STUDENT** : Étudiant inscrit aux cours
- **TEACHER** : Enseignant qui crée des cours et entre des notes

### Navigation Dynamique

La navigation change selon le rôle de l'utilisateur :

- **Student** : Accès aux écrans Home, Courses, Subscriptions, Grades, Final Grade
- **Teacher** : Accès aux écrans Home, Courses, Students, Grade Entry

### Restrictions d'Accès

- **Étudiants** : Ne peuvent pas entrer leurs propres notes
- **Enseignants** : Peuvent créer des cours et entrer des notes pour leurs étudiants
- **Filtrage** : Les étudiants ne voient que les cours de leur niveau

---

## Business Logic

### Calcul de la Note Pondérée

La note finale pondérée est calculée selon la formule :

```
Note Pondérée = Σ(score × ECTS) / Σ(ECTS)
```

**Exemple** :
- Cours 1 : Score 15, ECTS 6 → 15 × 6 = 90
- Cours 2 : Score 12, ECTS 4 → 12 × 4 = 48
- Total points : 138
- Total ECTS : 10
- **Note pondérée : 138 / 10 = 13.8**

**Implémentation** : `SubscribeRepository.calculateWeightedGrade()`

### Validation des Scores

- **Échelle** : 0..20
- **Validation** : Côté UI (formulaire) et ViewModel
- **Feedback** : Messages d'erreur Material 3

---

## Navigation

### Routes Principales

```kotlin
object Routes {
    // Auth
    const val SPLASH = "splash"
    const val LOGIN = "login"
    const val REGISTER = "register"
    
    // Student
    const val STUDENT_HOME = "student_home"
    const val STUDENT_COURSES = "student_courses"
    const val STUDENT_SUBSCRIBES = "student_subscribes"
    const val STUDENT_GRADES = "student_grades"
    const val STUDENT_FINAL_GRADE = "student_final_grade"
    
    // Teacher
    const val TEACHER_HOME = "teacher_home"
    const val TEACHER_COURSES = "teacher_courses"
    const val TEACHER_STUDENTS = "teacher_students/{courseId}"
    const val TEACHER_GRADE_ENTRY = "teacher_grade_entry/{courseId}/{studentId}"
}
```

### Navigation Graph

Le graphe de navigation est défini dans `AppNavHost.kt` :

1. **Splash** : Point d'entrée, redirige vers Login ou Home selon l'état
2. **Auth** : Login/Register
3. **Student Routes** : Navigation avec BottomNavigation
4. **Teacher Routes** : Navigation avec BottomNavigation

### Gestion de la Déconnexion

La déconnexion est gérée globalement dans `AppNavHost` :

- Détection de `AuthState.LoggedOut`
- Navigation automatique vers Login
- Nettoyage de la back stack

---

## Guide de Démarrage

### Prérequis

- **Android Studio** : Version récente (Hedgehog ou plus récent)
- **JDK 17** : Installé et configuré
- **SDK Android** : API 29 minimum (API 33+ recommandé)
- **Gradle** : 8.0+

### Installation

1. **Cloner/Ouvrir le projet**
   ```
   Ouvrir Android Studio → File → Open → Sélectionner II3510_CourseAPP_2526
   ```

2. **Synchroniser Gradle**
   ```
   File → Sync Project with Gradle Files
   ```
   (Première fois : peut prendre plusieurs minutes)

3. **Vérifier la configuration**
   - File → Project Structure → SDK Location
   - Vérifier JDK = 17
   - Vérifier `compileSdk = 36`, `targetSdk = 35`, `minSdk = 29`

4. **Créer un émulateur** (si nécessaire)
   ```
   Tools → Device Manager → Create Device
   - Modèle : Pixel 6 (ou similaire)
   - API : 33 (Android 13) ou supérieur
   ```

5. **Lancer l'application**
   ```
   Run → Run 'app' (Shift + F10)
   Sélectionner le module : scrudstudents
   ```

### Ligne de Commande (Alternative)

```powershell
# Naviguer vers le projet
cd "C:\Users\Taverny\OneDrive\Documents\isep\DEV_APP\II3510_CourseAPP_2526"

# Compiler
.\gradlew :app:scrudstudents:assembleDebug

# Installer sur appareil connecté
.\gradlew :app:scrudstudents:installDebug

# Vérifier appareils connectés
adb devices
```

### Premier Lancement

1. **Splash Screen** : Attendre la vérification d'authentification
2. **Login Screen** : Si pas connecté, affichage du formulaire de connexion
3. **Register** : Créer un compte (Student ou Teacher)
4. **Home** : Après connexion, redirection vers le dashboard selon le rôle

---

## Dépannage

### Problèmes de Compilation

#### Erreur "Daemon compilation failed"
- **Solution** : Vérifier que JDK 17 est configuré dans `gradle.properties`
- Vérifier `org.gradle.java.home=C:\\Program Files\\Java\\jdk-17`

#### Erreur "Out of memory"
- **Solution** : Vérifier `org.gradle.jvmargs=-Xmx1024m` dans `gradle.properties`

#### Erreur "Dependency requires at least JVM runtime version 11"
- **Solution** : Installer JDK 17 et configurer dans Android Studio
  - File → Settings → Build → Build Tools → Gradle → Gradle JDK = 17

#### Erreur "Schema export directory was not provided"
- **Solution** : Déjà corrigé dans `AppDatabase.kt` avec `exportSchema = false`

### Problèmes d'Exécution

#### Application bloque sur Splash Screen
- **Solution** : Timeout de 2 secondes implémenté. Si problème persiste :
  - Vérifier que `AuthViewModel.init` se termine correctement
  - Vérifier que `getCurrentUserSync()` fonctionne

#### Navigation ne fonctionne pas
- **Solution** : Vérifier que `AppNavHost` est bien utilisé dans `MainActivity`
- Vérifier que toutes les routes sont définies dans `Routes.kt`

#### Session ne persiste pas
- **Solution** : Vérifier que SharedPreferences fonctionne correctement
- Vérifier que `AuthRepository.saveSession()` est appelé après login/register

### Problèmes de Base de Données

#### Erreur "Cannot find migration"
- **Solution** : `fallbackToDestructiveMigration()` est utilisé pour développement
- En production, implémenter de vraies migrations

#### Données perdues après redémarrage
- **Solution** : Normal si `fallbackToDestructiveMigration()` est utilisé
- En production, implémenter des migrations

---

## Décisions Techniques

### Architecture

- **MVVM** : Séparation claire des responsabilités
- **Repository Pattern** : Abstraction de l'accès aux données
- **Unidirectional Data Flow** : Repository → ViewModel → UI

### State Management

- **StateFlow** : Exposé par les ViewModels
- **collectAsState()** : Utilisé dans Compose pour observer les changements
- **rememberSaveable** : Pour les champs de formulaire (rotation safe)

### Base de Données

- **Room** : Base de données locale SQLite
- **Version 2** : Après ajout de UserEntity et TeacherEntity
- **Migration** : `fallbackToDestructiveMigration()` pour développement

### Authentification

- **Hash** : SHA-256 (éducatif uniquement)
- **Session** : SharedPreferences
- **Timeout** : 30 minutes d'inactivité

### Validation

- **Scores** : 0..20 (validé côté UI et ViewModel)
- **ECTS** : > 0 (validé côté UI)
- **Niveaux** : Enum strict (P1, P2, P3, B1, B2, B3, A1, A2, A3, MS, PhD)

### UI/UX

- **Material Design 3** : Design system moderne
- **Dark/Light Theme** : Support automatique
- **Navigation** : BottomNavigation pour navigation principale
- **Feedback** : Messages d'erreur inline avec Material 3

### Coroutines & Flow

- **viewModelScope.launch** : Pour les opérations asynchrones
- **Flow** : Pour les données réactives
- **StateFlow** : Pour l'état observable
- **first()** : Pour obtenir la première valeur d'un Flow (au lieu de collect infini)

### Rotation d'Écran

- **ViewModel StateFlow** : Persiste l'état lors des changements de configuration
- **rememberSaveable** : Pour les champs de formulaire locaux
- **Navigation** : Persiste automatiquement avec Navigation Compose

---

## Références des Fichiers Clés

### Entités

- **`UserEntity.kt`** : Entité utilisateur pour authentification
- **`StudentEntity.kt`** : Entité étudiant avec FK vers User
- **`TeacherEntity.kt`** : Entité enseignant avec FK vers User
- **`CourseEntity.kt`** : Entité cours avec FK vers Teacher
- **`SubscribeEntity.kt`** : Entité inscription avec FKs vers Student et Course

### DAOs

- **`UserDao.kt`** : Requêtes pour User
- **`StudentDao.kt`** : CRUD pour Student
- **`TeacherDao.kt`** : CRUD pour Teacher
- **`CourseDao.kt`** : CRUD pour Course + filtres par niveau/enseignant
- **`SubscribeDao.kt`** : CRUD pour Subscribe + calculs

### Repositories

- **`AuthRepository.kt`** : Authentification + session management
- **`StudentRepository.kt`** : Opérations étudiant
- **`TeacherRepository.kt`** : Opérations enseignant
- **`CourseRepository.kt`** : Opérations cours
- **`SubscribeRepository.kt`** : Opérations inscriptions + calcul note pondérée

### ViewModels

- **`AuthViewModel.kt`** : Gestion authentification avec StateFlow
- **`StudentViewModel.kt`** : Gestion état étudiant
- **`TeacherViewModel.kt`** : Gestion état enseignant
- **`CourseViewModel.kt`** : Gestion état cours
- **`SubscribeViewModel.kt`** : Gestion état inscriptions

### Écrans

- **Auth** : `SplashScreen.kt`, `LoginScreen.kt`, `RegisterScreen.kt`
- **Student** : `StudentHomeScreen.kt`, `StudentCourseListScreen.kt`, `StudentSubscribeScreen.kt`, `StudentGradesScreen.kt`, `StudentFinalGradeScreen.kt`
- **Teacher** : `TeacherHomeScreen.kt`, `TeacherCourseListScreen.kt`, `TeacherStudentListScreen.kt`, `TeacherGradeEntryScreen.kt`

### Navigation

- **`Routes.kt`** : Définition de toutes les routes
- **`AppNavHost.kt`** : Graph de navigation principal avec routing dynamique

### Configuration

- **`AppDatabase.kt`** : Base de données Room
- **`AppModule.kt`** : Module Hilt pour injection de dépendances
- **`SCRUDApplication.kt`** : Application class avec @HiltAndroidApp

---

## Conclusion

Cette application démontre l'utilisation de **MVVM**, **Room**, **Hilt**, **Jetpack Compose**, et **Navigation Compose** pour créer une application Android moderne avec authentification, gestion des rôles, et business logic complexe (calcul de notes pondérées).

Le code suit les meilleures pratiques Android avec une architecture claire, une séparation des responsabilités, et une gestion d'état réactive via StateFlow et Flow.

---

**Dernière mise à jour** : Après corrections du chargement infini et amélioration de la navigation.

