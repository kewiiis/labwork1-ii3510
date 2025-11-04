# SCRUD Students - Application Android

Application Android de gestion universitaire avec authentification, gestion des rôles (Étudiant/Enseignant), gestion des cours, inscriptions et calcul de notes pondérées.

## 🚀 Démarrage Rapide

1. **Ouvrir dans Android Studio**
   ```
   File → Open → Sélectionner II3510_CourseAPP_2526
   ```

2. **Synchroniser Gradle**
   ```
   File → Sync Project with Gradle Files
   ```

3. **Lancer l'application**
   ```
   Run → Run 'app' (Shift + F10)
   Sélectionner le module : scrudstudents
   ```

## 📋 Technologies

- **Kotlin** + **Jetpack Compose** (Material Design 3)
- **MVVM** + **Repository Pattern**
- **Room** (Base de données locale)
- **Hilt** (Injection de dépendances)
- **Coroutines & Flow** (Programmation asynchrone)
- **Navigation Compose** (Navigation déclarative)

## 📖 Documentation Complète

Pour une documentation détaillée, voir **[docs/DOCUMENTATION.md](docs/DOCUMENTATION.md)** qui contient :

- Architecture complète
- Flux de données
- Structure du projet
- Fonctionnalités détaillées
- Guide de démarrage
- Dépannage
- Décisions techniques

## 🔑 Fonctionnalités Principales

- ✅ Authentification (Login/Register) avec timeout de session
- ✅ Gestion des rôles (Student/Teacher) avec navigation dynamique
- ✅ Gestion des cours avec validation ECTS et niveaux
- ✅ Inscriptions avec calcul de notes pondérées (Σ(score × ECTS) / Σ(ECTS))
- ✅ Validation des scores (0..20)
- ✅ Persistance de session via SharedPreferences

## ⚙️ Configuration

- **compileSdk** : 36
- **targetSdk** : 35
- **minSdk** : 29
- **JDK** : 17 (requis)

## 📱 Structure du Projet

```
app/scrudstudents/src/main/java/com/tumme/scrudstudents/
├── data/          # Entités, DAOs, Repositories
├── viewmodel/     # ViewModels avec StateFlow
├── ui/            # Écrans Compose (auth, student, teacher)
├── navigation/    # Routes et AppNavHost
├── di/            # Module Hilt
└── util/          # Utilitaires (PasswordHasher)
```

## 📚 Documentation

Voir **[docs/DOCUMENTATION.md](docs/DOCUMENTATION.md)** pour la documentation complète.
