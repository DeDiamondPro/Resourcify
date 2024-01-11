## Resourcify 1.3.0

### New Features

- Switch markdown rendering to [MineMark](https://github.com/DeDiamondPro/MineMark), this is a brand-new library for
  rendering markdown. Some notable improvements over the previous implementation include:
    - Support for raw HTML tags
    - Support for tables
    - Support for links in images
    - Support for center and right alignment
    - Support for horizontal rule elements

### Changes

- Use image resizing service (https://wsrv.nl/) on browse page, this increases image loading times on the browse page,
  especially on slower connections
- Better multithreading for fetching images, can increase loading speed when a lot of things are being loaded at once

### Bug Fixes
- Fix resource packs getting added at top after an update
- Fix not being able to update a pack if the old and new filename match

----------------------------------------------------------------------------------------------------

Looking for a top-notch Minecraft server host? BisectHosting has got you covered! Create your own server
at [bisecthosting.com/diamond](https://bisecthosting.com/diamond?r=resourcify+update) and use code **diamond** to get
25% of your first month while also supporting me!