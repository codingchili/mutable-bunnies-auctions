# Mutable Bunnies - Android Auction Client

The application is a companion application for the [Mutable Bunnies](https://github.com/codingchili/mutable-bunnies-server) online game, 
which is still in development. 
This application provides an on-the-go access to the in-game auction house, for buying and selling 
items. Due to the distributed design of the game, a central repository for items are required in 
order to execute orders across realm servers. This central entity is the Banking service, 
which includes an API for auctions as well. This ensures that the auction/banking service will 
have optimal locality for item data while also completely mitigates the risk of item duping 
for banking/auction operations. 

![screenshot](img/screenshot.png)

## Building

Requires min API v24.

```console
gradlew build
```