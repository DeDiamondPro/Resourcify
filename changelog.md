## Resourcify 1.3.0

### New Features

- Switch markdown rendering to [MineMark](https://github.com/DeDiamondPro/MineMark), this is a brand-new library for
  rendering markdown. Some notable improvements over the previous implementation include:
    - Support for raw HTML tags
    - Support for tables
    - Support for links in images
    - Support for center and right alignment
    - Support for horizontal rule elements
- Add support for localizations
  
If you find any issues please report them on [GitHub](https://github.com/DeDiamondPro/Resourcify) or in my [Discord](https://discord.gg/XtAuqsJWby)

### Changes

- Use image resizing service (https://wsrv.nl/) on browse page, this increases image loading times on the browse page,
  especially on slower connections
- Better multithreading for fetching images, can increase loading speed when a lot of things are being loaded at once
- Allow 3 retries for fetching important data
- Switch from kotlinx serialization to gson to decrease file size on legacy versions

### Bug Fixes
- Fix resource packs getting added at top after an update
- Fix not being able to update a pack if the old and new filename match
- Fix game crashing when not being able to fetch some resources
- Fix classes loading on a wrong class loader on modern forge and causing log spam
- Fix forge crashing when loading on server side (Resourcify is still client side only)

----------------------------------------------------------------------------------------------------

Looking for a top-notch Minecraft server host? BisectHosting has got you covered! Create your own server
at [bisecthosting.com/diamond](https://bisecthosting.com/diamond?r=resourcify+update) and use code **diamond** to get
25% of your first month while also supporting me!