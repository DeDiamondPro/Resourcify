## Resourcify 1.5.0

Please make sure to report any bugs and/or visual anomalies
on [Resourcify's GitHub](https://github.com/DeDiamondPro/Resourcify/issues) or in
the [Discord](https://discord.gg/XtAuqsJWby).

### New features

- **Added world support**: a plus button will now appear on the world selection screen, which you can use to download
  maps from CurseForge.
- Added config option to disable the buttons in specific screens (for example you can disable the world browser button,
  if you wish). To access Resourcify's config go to mod menu for fabric and click on Resourcify and then the config
  button, or for (Neo)Forge press the mods button, select Resourcify and then press the config button. If you would like
  to include a specific config in a modpack, all settings are saved in `.minecraft/config/resourcify.json`.
- Added a config option to choose if you want to use full resolution images (when available) instead of the lower
  resolution thumbnail images, this is usefully if you have a high-resolution screen and a fast internet connection.
- Added Korean translations by [Koala0107](https://github.com/Koala0107) ([#52](https://github.com/DeDiamondPro/Resourcify/pull/52)).
- Support for 1.21.3 fabric, Forge and NeoForge will come at a later date.

### Changes

- Fix modrinth gallery images using low-res images when opening them
- Use linear scaling for images instead of nearest neighbour scaling, fixes modrinth banners and logos looking pixelated
  due to modrinth switching to lower-resolution images for them.
- Switch to smaller thumbnail images for search and un-opened gallery images, improves gallery image loading speed,
  especially on slower connections
- Fix buttons getting re-created too many times, causing the fancy menu widget locator to change

----------------------------------------------------------------------------------------------------

Looking for a top-notch Minecraft server host? BisectHosting has got you covered! Create your own server
at [bisecthosting.com/diamond](https://bisecthosting.com/diamond?r=resourcify+update) and use code **diamond** to get
25% of your first month while also supporting me!