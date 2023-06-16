## New Features
- **Shaders support**: You can now browse shader packs in game using resourcify! This supports Iris (for fabric) and Optifine (for Forge).
Just press the + button in the shader gui (from Iris or Optifine) and a menu where you can browse shaders will open!
- **Datapack support**: You can now browse datapacks in game using resourcify by pressing the plus button in the datapack gui.

### Changes
- Hashes are now verified after a pack has been downloaded, this will detect if the file you downloaded is corrupted.
- Switched all hashes from SHA-1 to SHA-512

### Bug Fixes
- Fixed sorting dropdown sometimes opening immediately after opening the gui
- Fixed plus button being invisible on 1.18.2 forge
- Fixed plus button sometimes becoming invisible after opening datapack gui
- Fixed update checker (again), should actually work now
- Fixed cache clearing every time a page is switched instead of only when closing gui
- Fixed version page not displaying loader correctly sometimes