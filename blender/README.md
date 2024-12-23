# Emote emote animation tools

The `.blend` files are [Blender](https://www.blender.org/) projects.  
To use these, use the 2.83 LTS, or the latest version of Blender.  
[Emotecraft wiki](https://kosmx.gitbook.io/emotecraft/tutorial/custom-emotes) if you're stuck.  

`.bbmodel` files are models for [Blockbench](https://blockbench.net/) You can use them as well.  
To use them, you'll need to install [GeckoLib](https://geckolib.com/) plugin first.   

Models labled with `_bend` allow you to bend some bones and those labled with `_item` allow you to animate currently held items.  
Keep in mind that the visual for bending is incorrect in Blender/Blockbench, there won't be any gaps created by bending a bone in-game.  

> [!CAUTION]  
>  BENDING ON THE Z AXIS IS NOT SUPPORTED!!!  
>  And by Z axis I mean the one facing forward, I need to specify this since Blender uses a different coordinate system than MC.  
>  Make sure every bone's Z rotation value is always set to 0 in Blockbench and the same for Y rotation values in Blender!  

### If you don't like these
You can create your own program or edit the file by hand   
The emote format documentation is [here](https://github.com/KosmX/emotes/wiki/Emote.json)  
[Here](https://github.com/bigguy345/Blender-Minecraft-Animation/tree/main) is a Blender addon that lets you import and save animations + bend limbs on multiple axes.  
