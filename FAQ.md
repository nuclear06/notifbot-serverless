# NotifBot FAQ

## How do I add an app to NotifBot?

There are two ways to add apps:

**Method 1: From App List (Recommended)**

1. Open NotifBot app
1. Tap the menu (⋮) in the top-right corner
1. Select "Add App"
1. Browse or search for the app you want to add
1. Tap on the app to add it
1. You should see a Toast message as confirmation

**Method 2: Share from Google Play**

Basically, share the app from Google Play to NotifBot.
Here are the detailed steps:

1. Go to the Google Play page of the app you want to add
   ([example](https://play.google.com/store/apps/details?id=com.smartthings.android))
1. Find the Share button (last time I checked, it's in the `...` menu)
1. Choose NotifBot in the Share menu
1. You should see a Toast afterwards as confirmation

## How do I connect with Telegram?

You have to create your own Telegram bot and get the bot token. The app has instructions to configure the Telegram bot token when you first open it. The token is stored locally after encryption.

## Will other users see my notifications?

No. The Telegram Bot API requires specifying a receiver chat ID, which is unique to every user. Only you can see your notifications.