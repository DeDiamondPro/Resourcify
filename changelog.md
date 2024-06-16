## Resourcify 1.4.0

Please make sure to report any bugs and/or visual anomalies on [Resourcify's GitHub](https://github.com/DeDiamondPro/Resourcify/issues) or in the [Discord](https://discord.gg/XtAuqsJWby).

### Major new features
- Add support for CurseForge, you can now browse and install resource packs from CurseForge. There is a new dropdown on the browse page to switch between CurseForge and modrinth.
- Add settings GUI, allowing you to configure the default source. The config can be accessed via the mod menu (on fabric you need the `mod menu` mod to be able to access the config).
- Support for expandable dropdowns in project descriptions have been added.
- [MineMark](https://github.com/DeDiamondPro/MineMark) (Resourcify's markdown library) has been updated to [1.1.0](https://github.com/DeDiamondPro/MineMark/releases/1.1.0), adding support for css style tags, like text color and size.
- Dependencies of a project are now listed below the changelog of a version

### Other changes

- Convert backend to an abstract service api, allowing for easier support for more services
- Fix background breaking when using essential (or another mod with universalcraft) in mc 1.20.5+
- Fix crash in case of malformed html in markdown
- Fix localizations not working on 1.12.2 forge
- Suppress some errors that were causing log spam in some scenarios
- Added small bisect hosting ad to the top of the browse page, this is to help support the project and can be disabled in the new settings page

----------------------------------------------------------------------------------------------------

Looking for a top-notch Minecraft server host? BisectHosting has got you covered! Create your own server
at [bisecthosting.com/diamond](https://bisecthosting.com/diamond?r=resourcify+update) and use code **diamond** to get
25% of your first month while also supporting me!