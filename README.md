# TFT Teambuilder POC
A team builder app for Teamfight tactics, set 12.

<img src="/images/screenshot.png" alt="screenshot of app" width="360"/>

This app is a simple POC for making a team builder app for Teamfight Tactics on mobile.

Websites such as [tactics.tools](https://tactics.tools/) and [Meta Tft](https://www.metatft.com/)
exist already as web apps that have this implementation. I just wanted to see if I could recreate 
the functionality in an Android app.

I am used an html version of tactics.tools on Set 12 (since I could not find the unit data in
Riot's API at the time).

### Item Integration

Experimental branch with item integration. Supports emblems, including unique emblems, and not being
able to drag emblems on units that already have that trait. Examples below:

<img src="/images/items.png" alt="screenshot of items" width="360"/>

<img src="/images/items.gif" alt="screenshot of app" width="360"/>

### Legal

This app uses assets from tactics.tools and MetaTFT, but for non commercial purposes. If the assets
are ever changed or removed from their CDN, this app will cease to function as intended.

This app isn't endorsed by Riot Games and doesn't reflect the views or 
opinions of Riot Games or anyone officially involved in producing or 
managing Riot Games properties. Riot Games, and all associated properties are 
trademarks or registered trademarks of Riot Games, Inc.
