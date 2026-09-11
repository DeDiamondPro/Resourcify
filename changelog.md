## Resourcify 1.8.5

Please make sure to report any bugs and/or visual anomalies
on [Resourcify's GitHub](https://github.com/DeDiamondPro/Resourcify/issues) or in
the [Discord](https://dediamondpro.dev/discord).

- Add authentication to CurseForge downloads, starting 16/06/2026 this will be a requirement to download any file from
  CurseForge, **CurseForge downloads will not work without this update, therefore it is HIGHLY recommended you update**.
- Compatibility with VulkanMod: Resourcify's GUI was rendered upside down while VulkanMod was
  loaded because the offscreen texture flip compensating the OpenGL backend was applied twice.
  The flip is now only applied when VulkanMod is not loaded, and the mod can be run alongside it.

----------------------------------------------------------------------------------------------------

Looking for a top-notch Minecraft server host? BisectHosting has got you covered! Create your own server
at [bisecthosting.com/diamond](https://bisecthosting.com/diamond?r=resourcify+update) and use code **diamond** to get
25% of your first month while also supporting me!