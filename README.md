# Controlled Plant Growth for Spigot and Paper

Do you remember the times when you planted all your seeds, went on a long mining spree and came back to the same
progress at what you left? This plugin combats that.

My goal is to develop a lightweight plugin that can force plants to be grown in a specific timespan.
**It does not only manage plants placed by the player;
Plants placed by villagers and plants in general will be managed too.**

Plants with multiple age levels will reflect those changes as well.
F. e. a wheat field will grow continuously to feel as immersive as possible.

Here is a gif that shows how you can adjust the growth time and its resulting effects.
In this example the default setting of 20 minutes was changed to 5 seconds.

![GIF of planting potatoes and showcasing the growth setting](https://github.com/WladHD/ControlledPlantGrowth/blob/assets/assets/ezgif-7-4abf2ad084.gif?raw=true)

Feel free to open a new issue with ideas of new functionality or just feedback.
Especially regarding plants in development or on-hold refer to section
[All Planned and Supported Plants](#All-Planned-and-Supported-Plants).

### Tested on Server Software

- Paper 1.21 Build #44, #40 (experimental)

## Features

- Manages plants placed by the player as well as entities such as Villagers. Registers all plants on chunk load, perfect
  for plug in and play (can be turned off).
- Select how much time maximally has to pass in order for the plant type to fully mature.
- Plants that have multiple steps of growth (f. e. wheat) will grow continuously for immersive gameplay.
- Random ticks / "natural growth" is by default enabled to allow a more random growth (can be turned off).
- A player does not have to be in the chunk for the plants to grow.
- The plugin does not load chunks, making it resource friendly.

### Supported Plants

Supported Plants
- Wheat
- Beetroot
- Potatoes
- Carrots
- Sweet Berry Bush
- Cactus
- Sugar Cane
- Melons
- Pumpkins
- Bamboo
- Cocoa Beans
- Kelp

Supported Trees / Saplings
- Oak
- Birch
- Spruce
- Giant spruce (2x2 Mega-Tree)
- Jungle
- Giant jungle (2x2 Mega-Tree)
- Acacia
- Dark oak
- Cherry

#### All Planned and Supported Plants

- Y = Implemented
- D = In development
- R = Only if requested

<details>
<summary>expand to see all supported, planned and on-hold plants</summary>
refererence to https://minecraft.fandom.com/wiki/Crops

| Pant              | Implemented? |
|-------------------|--------------|
| Wheat Seeds       | Y            |
| Beetroot Seeds    | Y            |
| Carrot            | Y            |
| Potato            | Y            |
| Melon             | Y            |
| Pumpkin           | Y            |
| Bamboo            | Y            |
| Cocoa Beans       | Y            |
| Sugar Cane        | Y            |
| Sweet Berries     | Y            |
| Cactus            | Y            |
| Kelp              | Y            |
| Nether Wart       | Y            |
| Torchflower Seeds | R            |
| Pitcher Pod       | R            |
| Mushrooms         | R            |
| Sea Pickle        | R            |
| Chorus Fruit      | R            |
| Fungus            | R            |
| Glow Berries      | R            |

</details>

<details>
<summary>expand to see all supported, planned and on-hold trees (saplings)</summary>
refererence to https://minecraft.fandom.com/wiki/Tree#Types_of_trees

| Tree          | Implemented? |
|---------------|--------------|
| Oak           | Y            |
| Birch         | Y            |
| Spruce        | Y            |
| Giant spruce  | Y            |
| Jungle        | Y            |
| Giant jungle  | Y            |
| Acacia        | Y            |
| Dark oak      | Y            |
| Cherry        | Y            |
| Mangrove      | R            |
| Azalea        | R            |
| Huge fungus   | R            |
| Huge mushroom | R            |
| Chorus plant  | R            |

</details>

### Commands

|                          Command	                          |             Permission 	             |                                                                                                                                                                                              Description	                                                                                                                                                                                              |
|:----------------------------------------------------------:|:------------------------------------:|:------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------:|
| /cpg set <material> <time / [1,2,3...maxAge]> [timeUnit] 	 |     controlledplantgrowth.set 	      |                                                                                                                       Sets the time of a specified plant to mature, saves the new config and applies the changes to plants. Defining an array of growth steps is possible now. 	                                                                                                                       |
|                   /cpg info [material] 	                   |  controlledplantgrowth.info       	  |                                                                                                                                             Lists current configuration of plants and their time to mature.                                             	                                                                                                                                              |
|                      	/cpg forceload                       | controlledplantgrowth.forceload    	 | Forces all <br/><br/><br/><br/><br/><br/>loaded chunks to be scanned for plants, which have the `ignoreInAutomaticChunkAnalysis` set to `false`. Only effective when `useAggressiveChunkAnalysisAndLookForUnregisteredPlants` is set to `false`. If mentioned setting is set to true, the chunks will be automatically analyzed on load (default).                                                   	 |
|                         /cpg help                          |      controlledplantgrowth.help      |                                                                                                                                                                          Print the abbreviated version of this description :)                                                                                                                                                                          |

### Configuration Files

<details>
  <summary>... explaining `plantSettings.yml`</summary>

```yaml
settingsPageName: "default"
settingsVersion: "SETTINGS_V2"
disableNaturalGrowth: false
respectUnloadedChunks: true
useAggressiveChunkAnalysisAndLookForUnregisteredPlants: true
plantGrowthList:
  - material: "WHEAT"
    ignoreInAutomaticChunkAnalysis: false
    useTimeForPlantMature: false
    timeForPlantMature: 60
    timeForNextPlantGrowthInSteps:
      - 180
      - 180
      - 180
      - 240
      - 120
      - 120
      - 60
  - material: "BEETROOTS"
    ignoreInAutomaticChunkAnalysis: false
    useTimeForPlantMature: true
    timeForPlantMature: 1080
    timeForNextPlantGrowthInSteps: [ ]
  - material: "POTATOES"
    ignoreInAutomaticChunkAnalysis: false
    useTimeForPlantMature: true
    timeForPlantMature: 1080
    timeForNextPlantGrowthInSteps: [ ]
  - material: "CARROTS"
    ignoreInAutomaticChunkAnalysis: false
    useTimeForPlantMature: true
    timeForPlantMature: 1080
    timeForNextPlantGrowthInSteps: [ ]
  - material: "NETHER_WART"
    ignoreInAutomaticChunkAnalysis: false
    useTimeForPlantMature: true
    timeForPlantMature: 1800
    timeForNextPlantGrowthInSteps: [ ]
  - material: "SWEET_BERRY_BUSH"
    ignoreInAutomaticChunkAnalysis: false
    useTimeForPlantMature: true
    timeForPlantMature: 1080
    timeForNextPlantGrowthInSteps: [ ]
  - material: "MELON_STEM"
    ignoreInAutomaticChunkAnalysis: false
    useTimeForPlantMature: false
    timeForPlantMature: 60
    timeForNextPlantGrowthInSteps:
      - 120
      - 120
      - 120
      - 240
      - 120
      - 120
      - 60
      - 180
  - material: "PUMPKIN_STEM"
    ignoreInAutomaticChunkAnalysis: false
    useTimeForPlantMature: false
    timeForPlantMature: 60
    timeForNextPlantGrowthInSteps:
      - 120
      - 180
      - 60
      - 240
      - 120
      - 120
      - 60
      - 180
  - material: "BAMBOO"
    ignoreInAutomaticChunkAnalysis: true
    useTimeForPlantMature: false
    timeForPlantMature: 60
    timeForNextPlantGrowthInSteps:
      - 70
      - 76
      - 65
      - 69
      - 78
      - 62
      - 64
      - 63
      - 72
      - 68
      - 68
      - 81
      - 64
      - 53
      - 67
  - material: "COCOA"
    ignoreInAutomaticChunkAnalysis: false
    useTimeForPlantMature: true
    timeForPlantMature: 960
    timeForNextPlantGrowthInSteps: [ ]
  - material: "KELP"
    ignoreInAutomaticChunkAnalysis: true
    useTimeForPlantMature: false
    timeForPlantMature: 960
    timeForNextPlantGrowthInSteps:
      - 48
      - 49
      - 50
      - 41
      - 41
      - 41
      - 37
      - 38
      - 43
      - 42
      - 41
      - 40
      - 37
      - 36
      - 49
      - 40
      - 37
      - 45
      - 58
      - 42
      - 44
      - 42
      - 36
      - 56
      - 47
  - material: "AIR"
    ignoreInAutomaticChunkAnalysis: true
    useTimeForPlantMature: true
    timeForPlantMature: 1200
    timeForNextPlantGrowthInSteps: [ ]
maximumAmountOfPlantsInATimeWindowCluster: 1
maximumTimeWindowInMillisecondsForPlantsToBeClustered: 1
```

</details>

<details>
  <summary>... explaining `config.yml`</summary>

```yaml
notifyOnSpigotRelease: true
notifyOnGitHubExperimentalRelease: false
loadPlantSettingsFromDatabase: false
enableDebugLog: false
activeSettingsPage: "default"
hibernateConfigPlantSettings:
  hibernate.connection.driver_class: "org.h2.Driver"
  hibernate.connection.url: "jdbc:h2:./plugins/ControlledPlantGrowth/data/plantSettings;AUTO_SERVER=TRUE"
  hibernate.hbm2ddl.auto: "update"
  hibernate.dialect: "org.hibernate.dialect.H2Dialect"
  hibernate.show_sql: "false"
  hibernate.connection.password: ""
  hibernate.connection.username: "sa"
hibernateConfigLocalPlantCache:
  hibernate.connection.driver_class: "org.h2.Driver"
  hibernate.connection.url: "jdbc:h2:./plugins/ControlledPlantGrowth/data/plantCache;AUTO_SERVER=TRUE"
  hibernate.hbm2ddl.auto: "update"
  hibernate.dialect: "org.hibernate.dialect.H2Dialect"
  hibernate.show_sql: "false"
  hibernate.connection.password: ""
  hibernate.connection.username: "sa"
currentSettingsVersion: "SETTINGS_V2"
```

</details>

# Developed Using

- IntelliJ IDEA 2024.1.4 (Ultimate Edition) Build #IU-241.18034.62
- IntelliJ Plugins
    - com.demonwav.minecraft-dev (2024.1-1.7.6)
- Oracle OpenJDK 22.0.1 (language level set to 8)

# LICENSE

MIT License
