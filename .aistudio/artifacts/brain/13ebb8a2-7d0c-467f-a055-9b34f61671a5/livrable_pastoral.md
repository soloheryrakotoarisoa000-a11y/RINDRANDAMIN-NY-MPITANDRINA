# RINDRANDAMIN'NY MPITANDRINA
### Application Professionnelle de Gestion Pastorale Réformée
**Créateur officiel** : Solohery RAKOTOARISOA

---

## 1. Fichier APK et Téléchargement
Le binaire Android `.apk` a été assemblé avec succès :
- **Fichier APK généré** : `app/build/outputs/apk/debug/app-debug.apk`
- **Copie dans les artefacts** : `RINDRANDAMIN_NY_MPITANDRINA.apk` (24 Mo)
- **Application ID** : `com.aistudio.pastoral.rkqpx`
- **Version** : 1.0 (SDK cible 36, SDK min 24)
- **Signature** : debug / release configurée

> **Comment récupérer et installer l'APK :**
> 1. Dans l'interface de Google AI Studio, cliquez sur le menu des paramètres ou l'icône de téléchargement en haut à droite de l'écran pour exporter le projet sous forme d'archive ZIP ou télécharger l'APK / AAB compilé.
> 2. Transférez le fichier `app-debug.apk` (ou `RINDRANDAMIN_NY_MPITANDRINA.apk`) sur votre téléphone Android.
> 3. Ouvrez le fichier sur votre téléphone pour installer l'application (autorisez l'installation d'applications de sources inconnues si le système vous le demande).
> 4. L'application s'exécute immédiatement, **100% hors ligne**, sans connexion requise et sans inscription.

---

## 2. Devise Biblique Fondamentale
Présente au cœur de l'écran d'accueil et dans la page « À propos », inaltérée et strictement en malagasy :

> « Mahatoky izao teny izao: Raha misy olona maniry ny asan'ny mpitandrina, dia maniry asa tsara izy. »  
> **I Timoty 3.1**

---

## 3. Synthèse des 8 Modules Intégrés

| Module | Fonctionnalités clés | Exportation |
| :--- | :--- | :--- |
| **1. Prédications** | Contexte historique, exégèse, théologie, plan homilétique, applications, illustrations, conclusion, prière, progression (0-100%) | **PDF** officiel |
| **2. Réunions** | Ordre du jour, présences, délibérations, décisions, responsables et échéances, procès-verbal (PV) | **PDF** du PV |
| **3. Événements** | Conférences, séminaires, cultes spéciaux, mariages, funérailles, logistique, intervenants, budget vs réel | Suivi budgétaire |
| **4. Agenda pastoral** | Calendrier des rendez-vous et visites, priorités, alertes de retard, rappels programmés locaux | Notifications locales |
| **5. Administration** | Répertoire du bureau, commissions, départements (appels et courriels en 1 clic) et documents légaux | Archivage local |
| **6. Finances** | Recettes, dépenses, dîmes et offrandes, devises (Ariary `Ar`, Euro `€`, Dollar `$`), solde net en direct | **PDF** Bilan |
| **7. Archives** | Recherche textuelle globale multi-entités et filtres rapides | Recherche instantanée |
| **8. Paramètres** | Choix de langue (Malagasy, Français, English), thème clair/sombre, code PIN à 4 chiffres, sauvegarde/restauration JSON | Sauvegarde **JSON** |

---

## 4. Spécifications Techniques et Sécurité
- **Plateforme** : Android natif (Kotlin, Jetpack Compose, Material Design 3).
- **Persistance des données** : Base de données locale Room (SQLite `pastoral_ministry.db`) pré-remplie avec des données d'exemple réalistes.
- **Export PDF** : Moteur vectoriel natif Android (`android.graphics.pdf.PdfDocument`), 100% autonome et sans dépendance externe.
- **Sécurité** : Protection facultative par code PIN à 4 chiffres avec pavé numérique dédié.
- **Confidentialité** : Aucune donnée personnelle transmise à des tiers, aucun serveur externe, fonctionnement autonome garanti sur tout smartphone Android.
