# TFT Teambuilder POC
A team builder app for Teamfight tactics, set 12.

![Screenshot of App](/images/screenshot.png "Screenshot of app")

This app is a simple POC for making a team builder app for Teamfight Tactics on mobile.

Websites such as [tactics.tools](https://tactics.tools/) and [Meta Tft](https://www.metatft.com/)
exist already as web apps that have this implementation. I just wanted to see if I could recreate 
the functionality in an Android app.

I am used an html version of tactics.tools on Set 12 (since I could not find the unit data in
Riot's API at the time).

### Item Integration

Experimental branch with item integration. Supports emblems, including unique emblems, and not being
able to drag emblems on units that already have that trait. Examples below:

![Screenshot of Items](/images/items.png "Screenshot of Items")

![Video of Items](/images/items_2.mov "Video of Items")

### Legal

This app uses assets from tactics.tools and MetaTFT, but for non commercial purposes. If the assets
are ever changed or removed from their CDN, this app will cease to function as intended.

This app isn't endorsed by Riot Games and doesn't reflect the views or 
opinions of Riot Games or anyone officially involved in producing or 
managing Riot Games properties. Riot Games, and all associated properties are 
trademarks or registered trademarks of Riot Games, Inc.
