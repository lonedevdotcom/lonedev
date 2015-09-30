<a href='http://www.twitter.com/lonedev'><img src='http://twitter-badges.s3.amazonaws.com/follow_bird-b.png' alt='Follow me on Twitter' /></a>

Hi,

My name is Richard Hawkes. As the project name suggests, I am a lone developer. I have been tinkering with the JMonkey Engine and Blender for some time now. So this project is going to be my very first "real" game using these tools. Details on the game itself can be found on the wiki (SpaceHoops).

It will be fully open-source and I want EVERYONE to feel free to grab the source code and scrutinize.

Hey, and why not (oh please do) follow me on Twitter as well. It would be good to know who is keeping an eye on my work! Links at the top and bottom of this page.

<img src='http://farm5.static.flickr.com/4021/4655508859_171bd6fc76_b.jpg' alt='Latest SpaceHoops screenshot' width='640' height='480' />

Latest Twitter feeds...
<wiki:gadget url="http://google-code-feed-gadget.googlecode.com/svn/trunk/gadget.xml" up\_feeds="http://twitter.com/statuses/user\_timeline/150309989.rss" width="500px" height="340px" border="1"/>


---

## 3 August 2012 - Lonedev's Alive! ##
Yes, it's true. I never really went away, but managed to get so many little fiddly bits done and never finished that I lost all traction. But the latest little promotion from JME (http://tinyurl.com/cdcxqlt) has stirred my appetite. I have a project that is in dire need of looking at, and I want to get it in a "lookable" state! Not finished you understand. It's a tower defence game and I have a lot of pieces in place... Stay tuned :-)

---

## 15 June 2010 - Bloody rotation ##
I've finally (I think) started to get somewhere with working out how much rotation is needed to point at an object. You effectively need to clone your existing spaceships location and rotation and use the `lookAt(...)` method. This will rotate appropriately to the new location. You can then see how much rotation is needed. I haven't got my head around the results yet, but at least the values are changing as I move around.

---

## 8 June 2010 - Nowhere fast, so wiki what you already know! ##
Well I have been bouncing around looking for a clear definition of Nifty/JME integration and found nothing :-( I really don't want to battle my way through this with so little documentation. Maybe I don't have a choice! Oh well, in the mean time I thought I'd outline the steps I made to get Bitmap (truetype) fonts in to JME. It's pretty sweet. I can now get any font I like in there... And alpha worked with zero hair-pulling! Details are on the BitmapFont wiki page.

---

## 31 May 2010 - At last... Text on the screen! ##
Man, it's been a labour of love figuring out how to get text on the screen. Turns out the BitmapFont class was exactly what I was looking for! There's a new revision (0.02) out there now for your review. You will probably need to adjust the `main(...)` method to suit your PC. Set samples to zero is always a good start. For those who don't fancy the endeavour, <a href='http://www.youtube.com/watch?v=5jp1BVt9I7Q'>HERE IS A VIDEO OF THE LOOK SO FAR</a>. Please note the frame rate is much much better than this. I had to shrink it to 20fps (I think) to keep the file size down.

---

## 28 May 2010 - [Revision 0](https://code.google.com/p/lonedev/source/detail?r=0).01 is out there! ##
I'm definitely getting there. I have managed to get proof-of-concept working on the HUD overlay and sorted out movement. There's quite a lot more to be done, but you can take version 0.01 out for a drive if you like.

---

<a href='http://www.twitter.com/lonedev'><img src='http://twitter-badges.s3.amazonaws.com/follow_bird-b.png' alt='Follow me on Twitter' /></a>