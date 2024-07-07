# ControlledPlantGrowth for Spigot and Paper

Do you remember the times when you planted all your seeds, went on a long mining spree and came back to the same
progress at what you left? This plugin combats that.

My goal is to develop a plugin that can force plants to be grown in a specific timespan.
**It is not dependent on solely the player;
Plants placed by villagers and plants in general will be managed too.** 

Plants with multiple age levels will reflect those changes on each level as well.
So you won't have a wheat field that instantly grows, which is not immersive.
The change will influence all age levels, too.

Here is a gif of how you can adjust the grow time and its effects.
The default setting of 20 minutes was changed to 5 seconds.
![GIF of planting potatoes and showcasing the growth setting](https://github.com/WladHD/ControlledPlantGrowth/blob/assets/assets/ezgif-7-4abf2ad084.gif?raw=true)

That being said, if someone stumbles upon this site and actually tries my plugin, feel free to open a new issue with
ideas of new functionality or just feedback.

### Tested on Server Software

- Paper 1.21 Build #44, #40 (experimental)

## Features

- Manages plants placed by the player as well as entities such as Villagers. Registers all plants on chunk load,
  perfect for plug in and play (can be turned off).
- Select how much time maximally has to pass in order for the plant type to fully mature.
- Plants that have multiple steps of growth (f. e. wheat) will grow continuously for immersive gameplay.
- Random ticks / "natural growth" is by default disabled and is only managed by my plugin. (can be turned off)
- A player does not have to be in the chunk for the plants to grow.
- The plugin does not keep the chunk loaded like a chunk loader, making it more efficient.
- Talking about efficiency: if a lot of crops are to be managed, it is possible to group multiple growth processes.
  That results in the update of one or more plants at the same time (instead of being a couple of ticks apart).

### Supported Plants

- Wheat
- Beetroot
- Potatoes
- Carrots
- Sweet Berry Bush
- Cactus
- Sugar Cane

### In Development

- Saplings

### Commands

|             Command	             |             Permission 	             |                                                                                                                                                   Description	                                                                                                                                                    |
|:--------------------------------:|:------------------------------------:|:-----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------:|
| /cpg set <material> [timeUnit] 	 |     controlledplantgrowth.set 	      |                                                                                                      Sets the time of a specified plant to mature, saves the new config and applies the changes to plants 	                                                                                                       |
|           /cpg info 	            |  controlledplantgrowth.info       	  |                                                                                                   Lists current configuration of plants and their time to mature                                              	                                                                                                   |
|         	/cpg forceload          | controlledplantgrowth.forceload    	 | Forces all loaded chunks to be scanned for plants. Good if you dont have the `useAggressiveChunkAnalysisAndLookForUnregisteredPlants` set to `false`. You can register your farmland once. If mentioned setting is true, command should not have an effect.                                                     	 |
|            /cpg help             |      controlledplantgrowth.help      |                                                                                                                               Print the abbreviated version of this description :)                                                                                                                                |

### Configuration File
```yaml
# Set true if you want to save plantGrowthSettings using databaseHibernateSettings
loadConfigFromDatabase: false
enableDebugLog: false
plantGrowthSettings:
  # ignore
  id: null
  # ignore
  active: true
  # Sets if random ticks / natural growth are disabled for plants. I prefer the randomness, so I keep it off.  
  disableNaturalGrowth: false
  # debug option
  showInfoWhenDefaultSettingIsUsed: false
  # If true all chunks on chunk load will be asynchronously analyzed for unregistered plants. I measured no impact, 
  # but if you want to disable it you can set following to false:
  useAggressiveChunkAnalysisAndLookForUnregisteredPlants: true
  plantGrowthList:
    # EXAMPLE OF PLANT WITH NON-LINEAR GROWTH (wheat has 8 age steps, so we need 7 entries)
    - material: "WHEAT"
      # Set to false if you want to use non-linear growth
      useTimeForPlantMature: false
      timeForPlantMature: 1
      timeForNextPlantGrowthInSteps:
        - 180
        - 180
        - 180
        - 240
        - 120
        - 120
        - 60
    # EXAMPLE OF PLANT WITH LINEAR GROWTH
    - material: "BEETROOTS"
      # Set to true if you want to use linear growth
      useTimeForPlantMature: true
      timeForPlantMature: 1080
      timeForNextPlantGrowthInSteps: [ ]
    - material: "POTATOES"
      useTimeForPlantMature: true
      timeForPlantMature: 1080
      timeForNextPlantGrowthInSteps: [ ]
    - material: "CARROTS"
      useTimeForPlantMature: true
      timeForPlantMature: 1080
      timeForNextPlantGrowthInSteps: [ ]
    - material: "NETHER_WART"
      useTimeForPlantMature: true
      timeForPlantMature: 1800
      timeForNextPlantGrowthInSteps: [ ]
    - material: "SWEET_BERRY_BUSH"
      useTimeForPlantMature: true
      timeForPlantMature: 1080
      timeForNextPlantGrowthInSteps: [ ]
      # EXAMPLE OF ATTACHED PLANT WITH NON-LINEAR GROWTH
      # (melon has 8 age steps and one where the fruit is grown, so in total 9)
      # ERGO: we need 8 entries of times (last time is when the root is mature 
      # and a melon block wants to spawn)
    - material: "MELON_STEM"
      useTimeForPlantMature: false
      timeForPlantMature: 1
      timeForNextPlantGrowthInSteps:
        - 120
        - 120
        - 120
        - 240
        - 120
        - 120
        - 60
        - 180
    # same concept as in MELON_STEM
    - material: "PUMPKIN_STEM"
      useTimeForPlantMature: false
      timeForPlantMature: 1
      timeForNextPlantGrowthInSteps:
        - 120
        - 180
        - 60
        - 240
        - 120
        - 120
        - 60
        - 180
    # DEFAULT SETTING
    - material: "AIR"
      useTimeForPlantMature: true
      timeForPlantMature: 1200
      timeForNextPlantGrowthInSteps: [ ]
  # EXPERIMENTAL EFFICIENCY SETTINGS
  # Number of plants that can maximally grow per group
  maximumAmountOfPlantsInATimeWindowCluster: 1
  # Maximal difference between update timers to consider plants part of one growth step
  # Those plants will be grouped to one update time, following the max amount in maximumAmountOfPlantsInATimeWindowCluster
  maximumTimeWindowInMillisecondsForPlantsToBeClustered: 1
# Database setting, where all registered plants will be saved.
# Optionally can be used for settings as well if loadConfigFromDatabase is true
databaseHibernateSettings:
  hibernate.connection.driver_class: "org.h2.Driver"
  hibernate.connection.url: "jdbc:h2:./plugins/ControlledPlantGrowth/data/db;AUTO_SERVER=TRUE"
  hibernate.hbm2ddl.auto: "update"
  hibernate.dialect: "org.hibernate.dialect.H2Dialect"
  hibernate.connection.password: ""
  hibernate.connection.username: "sa"
```

# LICENSE

MIT License