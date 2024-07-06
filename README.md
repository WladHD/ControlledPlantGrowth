# ControlledPlantGrowth for Spigot and Paper

Do you remember the times when you planted all your seeds, went on a long mining spree and came back to the same
progress at what you left? This plugin combats that.

This is a project that I'm working on mainly for my use cases. It is fairly new and I expect myself to implement the
main types of growable plants.

## Features

- Select how much time maximally has to pass in order for the plant type to fully mature.
- Plants that have multiple steps of growth (f. e. wheat) will grow continuously for immersive gameplay.
- Currently the plugin affects only player placed plants.
- Random ticks are not disabled for managed plants (yet). I'm planning on
  introducing a boolean setting for that in the near future.
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

### Tested on Server Software
- Paper 1.21 Build #44, #40 (experimental)

## Personal Note

My goal is to develop a plugin that can adjust growing rates. The player can be online AS WELL AS offline or
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