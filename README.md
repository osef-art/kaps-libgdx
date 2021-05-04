# ![hi](android/assets/img/icons/icon.png "hey") KAPS (v2) ![there](android/assets/img/icons/icon.png "you")

A *'Dr. Mario'-like* colorful mini-game. Match the colored capsules and 
get rid of every germ in the grid ! 🧪

## LAUNCH THE GAME 🎮
⚠ You must have [**Java 11** or +](https://www.oracle.com/java/technologies/javase/jdk11-archive-downloads.html)
installed to play the game.

#### WINDOWS
- Click on `launch.bat`

#### LINUX
- Execute `./launch.sh`

...or place yourself in the root directory,
open a terminal and launch the command:
```bash
java -jar kaps-box2d.jar
```


## HOW TO PLAY 🕹

#### 💊 In-game
`⬅`, `➡` : **move** the capsule left/right  
`⬆` : **flip** the capsule  
`⬇` : **dip** the capsule of one row  
`[SPACEBAR]` : **drop** the capsule at the bottom  
`🇭` : save gelule in **HOLD**

#### 💊 General 
`🇵` : **pause** the game  
`🇶` : **exit** the game

---

## RULES 📜
Move falling capsules 💊 in the grid and make matches of **4 tiles** of the same color or more 
to destroy them.  
Destroy tiles of a sidekick's color to fill its **mana gauge** and unleash its attack !

![quick gameplay](android/assets/img/screens/KAPS-clip.gif "Quick gameplay")

Smash **every germ** 🦠 of the grid to win !  
But make sure not to exceed the grid ! Beware, the game gets faster over time.


## SIDEKICKS 🤜‍🤛
| Name | | Mana | Dmg | Power |  
|---:|:---:|:---:|:---:|:---|   
| JIM    | ![JIM   ](android/assets/img/sidekicks/Jim_0.png "Jim")       | 20       | 1 | Slices a random object and all tiles on the same line
| SEAN   | ![SEAN  ](android/assets/img/sidekicks/Sean_0.png "Sean")     | 20       | 2 | Hits a random object and adjacent tiles  
| ZYRAME | ![ZYRAME](android/assets/img/sidekicks/Zyrame_0.png "Zyrame") | 20       | 2 | Slices two random germs  
| PAINT  | ![PAINT ](android/assets/img/sidekicks/Paint_0.png "Paint")   | 10       | 0 | Paint 5 random caps
| COLOR  | ![COLOR ](android/assets/img/sidekicks/Color_0.png "Color")   | 4 turns  | 0 | Generates a gelule with both caps of same color
| MIMAPS | ![MIMAPS](android/assets/img/sidekicks/Mimaps_0.png "Mimaps") | 15       | 2 | Hits 3 random objects  
| BOMBER | ![BOMBER](android/assets/img/sidekicks/Bomber_0.png "Bomber") | 13 turns | 1 | Generates an explosive gelule
| SNIPER | ![SNIPER](android/assets/img/sidekicks/Sniper_0.png "Sniper") | 15       | 3 | Shoots a random germ
| RED    | ![RED   ](android/assets/img/sidekicks/Red_0.png "Red")       | 25       | 2 | Slices a random object and all tiles on the same column
| XERETH | ![XERETH](android/assets/img/sidekicks/Xereth_0.png "Xereth") | 25       | 1 | Slices a random object and all tiles on the same diagonals  
| ???    | ? | ? | ? | (Coming soon !)

## GERMS 🦠
| Name | | Cooldown | Power |  
|---:|:---:|:---:|:---|   
| BASIC | ![BASIC](android/assets/img/1/germs/basic/idle_0.png "Basic") | - | Exists
| WALL  | ![WALL ](android/assets/img/2/germs/wall4/idle_0.png "Wall")  | - | Needs several hits (4 max.) to be destroyed
| VIRUS | ![VIRUS](android/assets/img/5/germs/virus/idle_0.png "Virus") | 8 | Turns a random tile into a virus
| THORN | ![THORN](android/assets/img/4/germs/thorn/idle_0.png "Thorn") | 5 | Destroys a random capsule among tiles around
| ???    | ? | 6 | Turns a random caps into a basic germ, or a random basic germ into a wall (2 HP), or can heal a wall (by 1 HP) (Coming soon !)


## SPECIAL CAPSULES ✨💊
| Name | | Effect |  
|---:|:---:|:---|   
| EXPLOSIVE | ![EXPLOSIVE](android/assets/img/7/caps/bomb_unlinked.png "Explosive") | Explodes when destroyed, hitting all tiles around
| ???       | ? | (Coming soon !)



### TIPS 💡

- Matches of **more than 4 tiles** decrease the associated sidekick's cooldown by one.
  For sidekicks with gauges, it adds a little bonus.
  
- Kill **viruses** first. They can easily ruin a game.

- Don't forget to use the **HOLD** feature !

- The choice of **sidekicks** can be decisive for some levels.