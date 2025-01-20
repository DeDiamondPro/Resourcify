<div align="center">

# Resourcify

</div>

Resourcify is a Minecraft mod to install resource-, shader-, datapacks or worlds and update resource- and shaderpacks
right within Minecraft. If you are looking for more information or to download Resourcify, please see
the [Modrinth](https://modrinth.com/mod/resourcify)
or [CurseForge](https://www.curseforge.com/minecraft/mc-mods/resourcify) page.

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

# Theming

Since 1.7.0, Resourcify supports some basic theming for resource packs and/or mod packs. As with any mod, you can
overwrite Resourcify's textures by including a file with the same name as the original texture in your Resource pack
under the `assets/resourcify/textures` folder
(see [here](https://github.com/DeDiamondPro/Resourcify/tree/master/src/main/resources/assets/resourcify/textures)).

Additionally, you can edit the colors Resourcify uses for it GUIs by including a file named
`assets/resourcify/colors.json` in your resource pack. You can see the format of this
file [here](https://github.com/DeDiamondPro/Resourcify/blob/master/src/main/resources/assets/resourcify/colors.json).
It uses hex, and can optionally include an extra 2 letters for opacity, you can
use [this site](https://rgbacolorpicker.com/rgba-to-hex) to convert from rgba to hex if needed. For a breakdown of what
the different fields influence in the GUI, see below.
<details>
<summary>Breakdown of fields.</summary>

- `text_primary`: The color of the main text in all GUIs, in the default theme this is white.
- `text_secondary`: The color of the secondary text in all GUIs, in the default theme this is light gray.
- `text_link`: The color used for text links, in the default theme this is a blue color.
- `text_warn`: The color of text used for warnings, used in the update GUI to display that updates are loading, that a
  version is up-to-date and that you have to wait to close the GUI until the updates have completed.
- `button_primary`: The color for the primary buttons (for example the install button), in the default theme this is a
  green color.
- `button_secondary`: The color used for the secondary buttons (for example the changelog button in the update GUI), in
  the default theme this is a light gray color.
- `checkbox`: The color for checkboxes, used in the filters in the browse page and in the config GUI, this is a light
  gray color in the default theme.
- `expandable`: The color of an expandable section in a project description (like the one you are reading now), by
  default this is a transparent black color.
- `background`: The color of all backgrounds, by default this is a transparent black color.
- `ad_background`: The color of the background behind the advertisement in the browse screen (if the ad is enabled). By
  default, this a transparent light blue color.
- `fullscreen_background`: The color used as an overlay to darken everything, this is used when you open a gallery image
  in full screen and when you try to close the update GUI while updates are being installed. By default, this is a
  transparent black color.
- `dropdown`: The color used by a dropdown when it is closed, or for unselected elements when it is opened. By default
  this is a lightly transparent black.
- `dropdown_selected` The color used by a selected element in a dropdown, by default this is a lightly transparent green
  color.

</details>