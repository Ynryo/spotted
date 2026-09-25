# **Spotted**

<p align="center">
  <img src="https://img.shields.io/badge/Platform-Android-3DDC84?style=for-the-badge&logo=android&logoColor=white" alt="Platform Android" />
  <img src="https://img.shields.io/badge/Language-Java-ED8B00?style=for-the-badge&logo=openjdk&logoColor=white" alt="Language Java" />
  <img src="https://img.shields.io/badge/Min_SDK-30-informational?style=for-the-badge" alt="Min SDK 30" />
  <img src="https://img.shields.io/badge/Target_SDK-36-informational?style=for-the-badge" alt="Target SDK 36" />
  <img src="https://img.shields.io/badge/License-GPL--3.0-blue.svg?style=for-the-badge" alt="License GPL-3.0" />
</p>

Une application Android moderne, ultra-réactive et fluide pour suivre **en temps réel** les trams,
bus et trains en France. Fini de poireauter à l'arrêt sans savoir si ton transport est déjà passé, à
l'heure ou complètement bloqué !

---

## 🌟 Fonctionnalités Clés

### 🗺️ Carte Interactive & Immersive

- **Vue Satellite & Relief** : Basculez entre différents modes de carte pour repérer instantanément
  les véhicules dans leur environnement urbain.
- **Mode Boussole & Suivi dynamique** : Verrouillez la caméra sur un véhicule pour suivre son trajet
  en direct avec adaptation continue du cap et de l'orientation.

### 📍 Marqueurs & Animations Haute Performance

- **Interpolation fluide des déplacements** : Les véhicules glissent naturellement le long de leur
  trajectoire au lieu de sauter de position en position.
- **Rendu intelligent & fluide** : Optimisation poussée de l'affichage garantissant 60+ FPS
  constants, même avec des dizaines de véhicules simultanés sur la carte.
- **Tracés d'itinéraires vectoriels** : Visualisation claire et élégante du tracé complet de chaque
  ligne sélectionnée.

### 🚆 Support Multi-Réseaux & Unités Multiples (UM)

- **Bus, Trams & Trains** : Suivi temps réel multi-réseaux pour les transports urbains et
  régionaux (TER, TGV, etc.).
- **Détection des rames accouplées (UM)** : Détection automatique et fusion intelligente des trains
  circulant en Unité Multiple avec affichage d'une timeline unifiée.

### ⏱️ Fiche Détail & Horaires en Temps Réel

- **Timeline d'arrêts interactive** :
    - Liste complète des arrêts restants avec indicateur de progression du trajet.
    - Calcul et mise en valeur immédiate du retard (code couleur vert / orange / rouge).
    - Défilement automatique pour les noms d'arrêts longs.
    - Logos officiels vectoriels des réseaux et transporteurs en haute définition.

### ⭐ Favoris & Personnalisation

- **Gestion des favoris** : Sauvegardez vos lignes et arrêts récurrents pour y accéder en un geste.
- **Menu latéral de filtrage** : Filtrez et explorez rapidement les véhicules par réseau, ligne ou
  type de transport.

---

## 🏗️ Architecture & Composants

```
fr.ynryo.spotted/
├── MainActivity.java                # Point d'entrée, orchestration de la carte et du cycle de vie
├── MarkerStopsDetailActivity.java   # Vue détaillée d'un véhicule (arrêts, timeline, retards)
├── LateralDrawerActivity.java       # Menu latéral de filtrage et de navigation
│
├── artists/                         # Moteurs de rendu visuel sur la carte
│   ├── MarkerArtist.java            # Dessin et cache des marqueurs personnalisés
│   └── RouteArtist.java             # Rendu des polylines et des tracés de lignes
│
├── managers/                        # Couche métier et contrôleurs
│   ├── FetchingManager.java         # Coordination globale de la récupération des données
│   ├── MapManager.java              # Contrôle et configuration de la Google Map
│   ├── FollowManager.java           # Gestion du suivi caméra d'un véhicule
│   ├── CompassManager.java          # Gestion de la boussole et de l'orientation
│   ├── SaveManager.java             # Persistance locale (SharedPreferences / Cache)
│   ├── favorite/                    # Gestion des lignes et arrêts favoris
│   ├── fetchers/                    # Fetchers spécialisés (BusTracker, Tchoo, Ynryo API)
│   └── um/                          # Gestion des trains en Unité Multiple (assemblage timeline)
│
├── services/                        # Clients réseau HTTP (Retrofit)
│   ├── ApiClientFactory.java        # Fabrique de clients HTTP configurés
│   ├── BusTrackerApiService.java    # Endpoints Bus-Tracker
│   ├── TchooApiService.java         # Endpoints Tchoo (trains)
│   └── YnryoApiService.java         # Endpoints dédiés
│
└── glideModule/                     # Pipeline Glide pour le décodage et l'affichage SVG
```

---

## 🛠️ Stack Technique

| Domaine                   | Technologie / Librairie      | Utilisation                                             |
|:--------------------------|:-----------------------------|:--------------------------------------------------------|
| **Langage**               | Java 11                      | Langage principal du projet                             |
| **Plateforme**            | Android SDK 30 → 36          | Support moderne des composants Android                  |
| **Cartographie**          | Google Maps SDK + Maps Utils | Rendu cartographique, clusters et calculs géographiques |
| **Réseau**                | Retrofit 2 + OkHttp + Gson   | Consommation asynchrone et typée des API REST           |
| **Images & Vectoriel**    | Glide + AndroidSVG           | Chargement performant des logos SVG des réseaux         |
| **Interface Utilisateur** | Material Design Components   | UI moderne, BottomSheets, animations et tiroir latéral  |

---

## 🚀 Installation & Configuration

### 1. Prérequis

- **Android Studio** (version Jellyfish ou plus récente recommandée)
- **JDK 11** ou supérieur configuré
- Un appareil Android (Android 11 / API 30 minimum) ou un émulateur compatible Google Play Services

### 2. Cloner le projet

```bash
git clone https://github.com/Ynryo/spotted.git
cd spotted
```

### 3. Configurer la clé API Google Maps

Créez un fichier `local.properties` à la racine du projet (s'il n'existe pas déjà) et ajoutez votre
clé API Google Maps :

```properties
GOOGLE_MAPS_API_KEY=VOTRE_CLE_API_GOOGLE_MAPS
```

> [!TIP]
> Assurez-vous que l'API **Maps SDK for Android** est bien activée sur votre console Google Cloud.

### 4. Compiler et lancer

Ouvrez le projet dans Android Studio, synchronisez Gradle, puis lancez le build :

```bash
./gradlew assembleDebug
```

---

## 🤝 Remerciements & Sources de Données

- **[Kevin Biojout](https://github.com/kevinbioj)** pour le projet
  open-source [bus-tracker-2](https://github.com/kevinbioj/bus-tracker-2) qui agrège les données des
  réseaux de transports en commun.
- Les différentes plateformes Open Data des métropoles et de la SNCF.

---

## 📄 Licence

Ce projet est distribué sous licence **GNU General Public License v3.0**. Consultez le
fichier [LICENSE](LICENSE) pour plus de détails.