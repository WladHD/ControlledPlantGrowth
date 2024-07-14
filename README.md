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
Especially regarding plants in development or on-hold, refer to section
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
- If you don't want a specific plant to be managed by my plugin (f. e. clash with other plugins),
  remove it (manually) from the `plantSettings.yml`.

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

|                          Command	                          |             Permission 	             |                                                                                                                                                                               Description	                                                                                                                                                                               |
|:----------------------------------------------------------:|:------------------------------------:|:------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------:|
| /cpg set <material> <time / [1,2,3...maxAge]> [timeUnit] 	 |     controlledplantgrowth.set 	      |                                                                                                        Sets the time of a specified plant to mature, saves the new config and applies the changes to plants. Defining an array of growth steps is possible now. 	                                                                                                        |
|                   /cpg info [material] 	                   |  controlledplantgrowth.info       	  |                                                                                                                              Lists current configuration of plants and their time to mature.                                             	                                                                                                                               |
|                      	/cpg forceload                       | controlledplantgrowth.forceload    	 | Forces all loaded chunks to be scanned for plants, which have the `ignoreInAutomaticChunkAnalysis` set to `false`. Only effective when `useAggressiveChunkAnalysisAndLookForUnregisteredPlants` is set to `false`. If mentioned setting is set to true, the chunks will be automatically analyzed on load (default).                                                   	 |
|                         /cpg help                          |      controlledplantgrowth.help      |                                                                                                                                                           Print the abbreviated version of this description :)                                                                                                                                                           |
|                             -                              |      controlledplantgrowth.view      |                                                                                                                                              Permission to be able to see the /cpg command(s) in the vanilla autocomplete.                                                                                                                                               |

### Configuration Files

<details>
  <summary>... explaining `plantSettings.yml`</summary>

```yaml
# Settings page name to identify the wanted setting in the database,
# if loadPlantSettingsFromDatabase in config.yml is true
# Otherwise no effect
settingsPageName: "default"
# Internal version of the config / settings content (will be used to merge settings / config without
# having to delete the plugin folder in the future)
# Don't change
settingsVersion: "SETTINGS_V2"
# Decide whether plants can grow if not especially told so by my plugin
# F. e. natural growth
# I did not test this extensively, should work if true.
# If you encounter bugs report please.
disableNaturalGrowth: false
# No effect yet (as of version 2.0.0)
respectUnloadedChunks: true
# If a new chunk is loaded, search for unregistered plants
# This is processed asynchronously and I haven't measured any
# performance impact (/tps).
# If set to false plants will be only registered on specific events
# f. e. if a player places a plant / villager harvests a crop etc.
useAggressiveChunkAnalysisAndLookForUnregisteredPlants: true
# THE DEFAULT SETTING "AIR" WAS REMOVED!
# IF YOU DELETE AN ENTRY FOR A PLANT, THE PLUGIN WILL STOP MANAGING IT
# If you don't want a specific plant to be managed (f. e. because of a clash with other plugins),
# remove it from this list
plantGrowthList:
  # EXAMPLE 1: Plant with unique timers for each growth step.
  # timeForNextPlantGrowthInSteps is false, so the array timeForNextPlantGrowthInSteps will be used.
  # Wheat has ages from 0 to 7 (7 growth steps, 8 unique ages)
  # The array needs to have 7 (!) elements then (if you define less, the sum is used instead until you fix it)
  # ALL NUMBERS ARE GIVEN IN SECONDS, ENTRY 180 = 3 MINUTES
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
  # EXAMPLE 2: Plant with constant timers for each growth step.
  # useTimeForPlantMature is true, so the time in timeForPlantMature will be used.
  # Beetroots have ages from 0 to 7 (7 growth steps, 8 unique ages) and the given time to mature is 1080 seconds.
  # So to reach the next age it will take approx. 154 seconds (x 7 = 1080s).
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
  # EXAMPLE 3: MELONS and PUMPKINS have a stem, that has the ages from 0 to 7
  # THE FRUIT is then grown, resulting in an age from 0 to 8 (8 growth steps, 9 unique ages).
  # Therefore, timeForNextPlantGrowthInSteps contains 8 elements
  # OR set useTimeForPlantMature to true and refer to Example 2
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
  # BAMBOO can grow to a max. of 16 blocks = an array of 15 is needed
  # or useTimeForPlantMature = true if you want to have constant growth (refer to EXAMPLE 1 and 2)
  - material: "BAMBOO"
    ignoreInAutomaticChunkAnalysis: true
    useTimeForPlantMature: false
    timeForPlantMature: 60
    timeForNextPlantGrowthInSteps:
      - 57
      - 70
      - 59
      - 76
      - 69
      - 66
      - 67
      - 67
      - 67
      - 74
      - 57
      - 78
      - 66
      - 76
      - 71
  - material: "COCOA"
    ignoreInAutomaticChunkAnalysis: false
    useTimeForPlantMature: true
    timeForPlantMature: 960
    timeForNextPlantGrowthInSteps: [ ]
  # KELP can grow to a max. of 26 blocks = an array of 25 is needed
  # or useTimeForPlantMature = true if you want to have constant growth (refer to EXAMPLE 1 and 2)
  - material: "KELP"
    ignoreInAutomaticChunkAnalysis: true
    useTimeForPlantMature: false
    timeForPlantMature: 960
    timeForNextPlantGrowthInSteps:
      - 49
      - 35
      - 36
      - 46
      - 37
      - 41
      - 43
      - 32
      - 47
      - 43
      - 24
      - 42
      - 41
      - 56
      - 50
      - 50
      - 42
      - 50
      - 57
      - 47
      - 49
      - 36
      - 38
      - 45
      - 44
  # OAK_SAPLING and saplings in general are (internally in my plugin) assigned 2 ages:
  # 0 (sapling) 1 (mature, tree).
  # The array timeForNextPlantGrowthInSteps would contain 1 element if useTimeForPlantMature is true.
  # Therefore, timeForPlantMature can always be used for saplings, since there is always only 1 growth step.
  - material: "OAK_SAPLING"
    ignoreInAutomaticChunkAnalysis: false
    useTimeForPlantMature: true
    timeForPlantMature: 960
    timeForNextPlantGrowthInSteps: [ ]
  - material: "BIRCH_SAPLING"
    ignoreInAutomaticChunkAnalysis: false
    useTimeForPlantMature: true
    timeForPlantMature: 960
    timeForNextPlantGrowthInSteps: [ ]
  - material: "SPRUCE_SAPLING"
    ignoreInAutomaticChunkAnalysis: false
    useTimeForPlantMature: true
    timeForPlantMature: 960
    timeForNextPlantGrowthInSteps: [ ]
  - material: "ACACIA_SAPLING"
    ignoreInAutomaticChunkAnalysis: false
    useTimeForPlantMature: true
    timeForPlantMature: 960
    timeForNextPlantGrowthInSteps: [ ]
  - material: "DARK_OAK_SAPLING"
    ignoreInAutomaticChunkAnalysis: false
    useTimeForPlantMature: true
    timeForPlantMature: 960
    timeForNextPlantGrowthInSteps: [ ]
  - material: "JUNGLE_SAPLING"
    ignoreInAutomaticChunkAnalysis: false
    useTimeForPlantMature: true
    timeForPlantMature: 960
    timeForNextPlantGrowthInSteps: [ ]
  - material: "CHERRY_SAPLING"
    ignoreInAutomaticChunkAnalysis: false
    useTimeForPlantMature: true
    timeForPlantMature: 960
    timeForNextPlantGrowthInSteps: [ ]
# EXPERIMENTAL EFFICIENCY SETTINGS:

# The plugin searches for overdue plants and searches for plants that have to yet be updated.
# maximumAmountOfPlantsInATimeWindowCluster is the time in milliseconds,
# where plants that have yet to be updated will be grouped together.
# F. e. if plants were planted 200ms apart from each other, you can set
# maximumAmountOfPlantsInATimeWindowCluster to 200 and all plants that were
# found in that time window will be updated on the same tick
# ... which results in theoretically less load on the server if used well
# f. e. you can remove 5 plant updates that are apart by only 2 - 3 ticks and grow them in only 1 step
maximumAmountOfPlantsInATimeWindowCluster: 1
# maximumTimeWindowInMillisecondsForPlantsToBeClustered is meant to be used alongside the previous setting.
# It sets the maximal amount of plants that can be fit in the defined timeslot defined in
# maximumAmountOfPlantsInATimeWindowCluster.
# F. e. you can have 34323 plants that fit the 200ms requirement, but only
# maximumTimeWindowInMillisecondsForPlantsToBeClustered will be handled per update cycle (2 - 3 ticks).
maximumTimeWindowInMillisecondsForPlantsToBeClustered: 1
```

</details>

<details>
  <summary>... explaining `config.yml`</summary>

```yaml
# Receive official release updates?
notifyOnSpigotRelease: true
# Receive experimental release updates?
notifyOnGitHubExperimentalRelease: false
# Use hibernateConfigPlantSettings instead of local plantSettings.yml
loadPlantSettingsFromDatabase: false
# Enable debug log?
enableDebugLog: false
# ID of settings page in database (hibernateConfigPlantSettings)
# You can have multiple presets and swap between them ...
# If loadPlantSettingsFromDatabase is false it has no effect
activeSettingsPage: "default"
# Configure the database connection if you want to save the plantSettings.yml in a (remote) database.
# Currently, this is configured to be connected to a local database in CPG's plugin folder
# Follow the instructions on https://www.tutorialspoint.com/hibernate/hibernate_configuration.htm
# to connect to a MySQL or PostgreSQL database.
# If you are stuck, open an issue on my GitHub and request an example for your database type.
hibernateConfigPlantSettings:
  hibernate.connection.driver_class: "org.h2.Driver"
  hibernate.connection.url: "jdbc:h2:./plugins/ControlledPlantGrowth/data/plantSettings;AUTO_SERVER=TRUE"
  hibernate.hbm2ddl.auto: "update"
  hibernate.dialect: "org.hibernate.dialect.H2Dialect"
  hibernate.show_sql: "false"
  hibernate.connection.password: ""
  hibernate.connection.username: "sa"
# LEAVE AS IS IF YOU DON'T KNOW WHAT YOU ARE DOING
# hibernateConfigLocalPlantCache is more or less an experimental rudiment, this database is ALWAYS used
# It is responsible for caching plant coordinates in real time, so it should be local for best performance
hibernateConfigLocalPlantCache:
  hibernate.connection.driver_class: "org.h2.Driver"
  hibernate.connection.url: "jdbc:h2:./plugins/ControlledPlantGrowth/data/plantCache;AUTO_SERVER=TRUE"
  hibernate.hbm2ddl.auto: "update"
  hibernate.dialect: "org.hibernate.dialect.H2Dialect"
  hibernate.show_sql: "false"
  hibernate.connection.password: ""
  hibernate.connection.username: "sa"
# Internal version of the config / settings content (will be used to merge settings / config without
# having to delete the plugin folder in the future)
# Don't change
currentSettingsVersion: "SETTINGS_V2"
```

</details>

# Developed Using

- IntelliJ IDEA 2024.1.4 (Ultimate Edition) Build #IU-241.18034.62
- IntelliJ Plugins
    - com.demonwav.minecraft-dev (2024.1-1.7.6)
- Oracle OpenJDK 22.0.1 (language level set to 8)

# Contributing / Using the Framework
You can either fork this project to integrate your personal changes into this plugin.
Or you can include this plugin as Maven dependency and use the
ControlledPlantGrowthManager to register your own PlantType.

Newest version:
````xml
<dependency>
  <groupId>de.wladtheninja</groupId>
  <artifactId>controlledplantgrowth</artifactId>
  <version>2.0.0</version>
</dependency>
````

# LICENSE

MIT License
