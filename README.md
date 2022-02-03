# DeluxeMenus-DeluxeShop

![Main Shop](https://i.imgur.com/7NkcUz5.jpg)
![Category Example](https://i.imgur.com/UiQWvTx.jpg)
![Search Feature](https://i.imgur.com/JWqarXw.jpg)
![Pages](https://i.imgur.com/xeGFP3c.jpg)

This shop is fully configurable and very easy to add items to!

## Setup
Copy all files from `\plugins\` to your plugins directory, although I do recommend editing your own config files vs replacing as your other plugin settings could be lost.

**Required Plugins:** [DeluxeMenus](https://www.spigotmc.org/resources/deluxemenus.11734/), [MyCommand](https://www.spigotmc.org/resources/mycommand.22272/), [CommandPrompter](https://www.spigotmc.org/resources/commandprompter.47772/)

**Required Expansions:**  `checkitem`, `javascript`, `player`, `str`, `math`.
Download these with `/papi ecloud download <expansion>` then type `/papi reload`.

## Info
 - Open the menu via `/shop`
 - Items can be added via the `config.yml` file found in the `DeluxeShop` folder. There is a full list of items in the generated files folder
 - The `selling` option can be changed to 1 of 3 options: Any placeholder, `individual`, or any number. Numbers/placeholders will multiply the buy cost to get the sell value.
 - `/shopsearch <search>` is the mycmd command for searching the shop, it will open it.
 - Support for filters and anti-filters via `/search`.
 - Tags can be added to items. If you have a filter, it will have to match one of the item tags EXACTLY for it to show in the results.

The custom items and gui textures I use as example can be [downloaded here](https://www.dropbox.com/s/mi8104yalt6eajt/custom%20pack.zip?dl=1)

## Commands

- /sell <item> <amount>  -- Sells a certain amount of an item - `deluxeshop.sell`
- /sell hand -- Sells the item in your main hand -- `deluxeshop.sell.hand`
- /sell handall -- Sells all items in your inventory that match the item in your main hand - `deluxeshop.sell.handall`
- /sell all -- Sells everything in your inventory that can be sold - `deluxeshop.sell.all`
- /sell menu -- Opens a menu for you to put items into and sell - `deluxeshop.sell.menu`
- /search <query> -- Sets the player's search filter. - `deluxeshop.search` *
- /search other <player> <query> -- Sets another player's search filter - `deluxeshop.search.other` *

*Valid queries can be seperated by `,` (Example `sand,concrete_powder`). If the value is `clear` It will clear the player's filter. If you put `anti:` before-hand, it will set the player's anti-filter.