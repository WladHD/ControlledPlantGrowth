# ControlledPlantGrowth for Spigot and Paper

Do you remember the times when you planted all your seeds, went on a long mining spree and came back to the same
progress at what you left? This plugin combats that.

This is a project that I'm working on mainly for my use cases. It is fairly new and I expect myself to implement the
main types of growable plants.
![GIF of planting potatoes and showcasing the growth setting](https://github.com/WladHD/ControlledPlantGrowth/blob/assets/assets/ezgif-7-4abf2ad084.gif?raw=true)

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

## Personal Note

My goal is to develop a plugin that can adjust growing rates. The player can be online as well as offline or
outside the chunk without using drastic measures such as chunk loading... they (iirc) don't send random ticks, which
are the reason for vanilla plant growth. :D

That being said, if someone stumbles upon this site and actually tries my plugin, feel free to open a new Issue with
ideas of new functionality or just feedback.

## Personal Programming Road

I quite like the general structure of this project. Plants can be put together using different concepts, making new
plant types fairly simple to implement.
~~The most overhead comes from the massive chungus of a database I added as dependency (for mainly the reason of not
wanting to do it myself). Don't get me wrong, Hibernate ORM with a H2 Database is bonkers. The problem lies in the
size of the finished .jar that exceeds 20 MB.
Although I don't dislike FatJars I don't think it is in the nature of Spigot Plugins to be that big.
Trying out the feature of `libraries` in `plugin.yml` did sadly not work, otherwise I wouldn't be even complaining here.
Maybe because of my server software (haven't tested it rigorously yet) Hibernates new ClassLoader (because it's a
standalone .jar now) for the love of god can't detect my
DTOClasses. Reverting the change and shading it into my plugin resolves the problem instantly. After 2 hours of
torturing myself with changing pointless stuff, I came to
the conclusion to
postpone this "problem". I'll firstly focus on the actual features, before implementing a lightweight database or
outsourcing the big dependencies to the server software.~~

FINALLY!!!
I found a way to relocate the loading of Hibernate the server software making my plugin desirably slim.
:)
The thread https://hub.spigotmc.org/jira/si/jira.issueviews:issue-html/SPIGOT-6569/SPIGOT-6569.html helped me out.
A simple BootstrapServiceRegistry was all it took for Hibernate to locate my smol classes.
Here I would like to make an honorable mention to https://github.com/Byteflux/libby. In the end, I decided against its
integration, but still enjoyed the functionality.
Maybe there will be a time when I rethink that decision, but for now Spigots/Papers lib loader seems to work fine.
With the newfound certainty, that I will keep the Hibernate framework, I will adjust the current database classes to be
more presentable and follow the normal conventions.

# LICENSE

MIT License