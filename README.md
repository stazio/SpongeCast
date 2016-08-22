# Sponge Cast
A simple scheduled broadcasting plugin for [Sponge](https://www.spongepowered.org/).

## Features
* Pre and Post delay timers to add even more control over messages.
* Several different modes to switch between:
    * **round-robin** where messages are sent in order contrinusly
    * **random** where a message is selected at random
    * **next** where each message defines what message will be sent next
* Use multiple groups so many broadcasts can be sent using different modes, or different timings.
* Easy to grasp configuration file

## Planned Features
- [ ] Twitter feeds
    - [ ] feed from user's posts
    - [ ] feed from tags
- [ ] Tumblr feeds
- [ ] RSS feeds
- [ ] in-game commands
- [ ] permissions

## Configuration
The configuration is easy to use.
Each message group can be titled whatever it want's to be. These groups have two parts, the **mode**, and the **messages**.
The mode can be either **round**, **random**, or **next**.
The messages are always a list and the **message** is what will be sent to the viewer.
Each message can have a **pre-delay** and **post-delay** setting.
**pre-delay** will wait X ammount of seconds before playing the message.
**post-delay** will wait X ammount of seconds after playing the message before moving on the next message
    (most useful in random or next mode).
**delay** is an alias of **pre-delay** and can be used alone as a delay in-between messages.