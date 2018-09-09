# ImageDownloader
This project contains code to download image from http url and show them into the recycler grid view , wihout using any kind of Image download library, (Glide,Picasso, Imagedownloader etc). It uses basic classes of android to achieve the same.

1. Used Handler Thread to make HTTP request so that one thread can be utilized for the all pagination request. 
2. For image loading, I have created ThreadPool of 5 threads which load images from the network.
3. Scaled down the downloaded bitmap to fit in recyclerview grid cell.
