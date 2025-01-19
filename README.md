<div align="center">

![Resourcify](https://github.com/DeDiamondPro/Resourcify/assets/67508414/6836d217-1d9e-439b-b8d3-eb64428375cd)

------------------------------------

![Browse Resource Packs, right from in Minecraft](https://github.com/DeDiamondPro/Resourcify/assets/67508414/61ab5d82-a58c-4cea-b0c3-25f808e690fa)
Resourcify allows you to browse all resource packs, data packs and shaders on modrinth right from in Minecraft!<br>Just
open the resource pack GUI and press the plus button in the top right.

------------------------------------

![Install a Resource Pack with the click of a button](https://github.com/DeDiamondPro/Resourcify/assets/67508414/b1e18d3e-7f04-4a96-a4b9-810173de1dfd)
Easily view the description, gallery and versions of a pack,<br>when you've found a pack you like you can easily install
it with one click!

------------------------------------

![Update packs easily right within Minecraft](https://github.com/user-attachments/assets/cc22855d-16b0-48d2-b446-030e4cc2e69a)
Easily view available updates and install all of them or just the one you want with one click!

------------------------------------

</div>

# Building

Resourcify uses stonecutter to compile against multiple Minecraft versions and loaders at once, this changes a bit about
how the mod is built. For more info about stonecutter, see their [wiki](https://stonecutter.kikugie.dev/).

Before your first build you need to run the following command to put the access wideners in the correct places, you will
need to run this every time you update an access widener.

```shell
./gradlew setupChiseledBuild
```

Then to build the mod run the following command, the mod jars will be in the `jars` folder after the build has finished.

```shell
./gradlew chiseledBuild
```

------------------------------------

[<img src="https://www.bisecthosting.com/partners/custom-banners/43c818dc-2c64-43d0-bf13-568c6037b79e.webp" alt="https://bisecthosting.com/diamond" width=100%>](https://bisecthosting.com/diamond?r=Resourcify+GitHub)
