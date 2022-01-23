# DeluxeMenus-DeluxeShop

This shop is fully configurable and very easy to add items to!

## Setup
Copy all files from here to your plugin directory. I do recommend editing your own config files vs replacing as your other plugin settings could be lost.

**Required Plugins:** [DeluxeMenus](https://www.spigotmc.org/resources/deluxemenus.11734/), [MyCommand](https://www.spigotmc.org/resources/mycommand.22272/), [CommandPrompter](https://www.spigotmc.org/resources/commandprompter.47772/)

**Required Expansions:**  `checkitem`, `javascript`, `player`, `str`, `math`.
Download these with `/papi ecloud download <expansion>` then type `/papi reload`.

## Info
 - Open the menu via `/shop`
 - Items can be added via the `deluxeshop.yml` file found in the `PlaceholderAPI` folder. There is a full list of items in the generated files folder
 - The `selling` option can be changed to 1 of 3 options: Any placeholder, `individual`, or any number. Numbers/placeholders will multiply the buy cost to get the sell value.
 - `/shopsearch <search>` is the mycmd command for searching the shop, it will open it.
 - Support for filters and anti-filters via deluxemenus meta `dmshop-filter` and `dmshop-antiFilter`.
 - Tags can be added to items. If you have a filter, it will have to match one of the item tags EXACTLY for it to show in the results.