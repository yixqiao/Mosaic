# Mosaic program

## Description
* Create an image mosaic composed of smaller images
* Made using Java (AWT for images)

## Example
![Image example](sample.jpg)
* Image made using faces from CelebA dataset

## Quickstart
1. Clone repository
2. Run `java -jar mosaic.jar averages -n 1000` to calculate averages for 1000 images
3. Run `java -jar mosaic.jar build -p img_in/lake.jpg -c 10 -s 2 -o lake_output.jpg` to build an image
4. The output image will be located at lake_output.jpg

## Details
The program works by finding the average color of each of the images to build from. It then goes through chunks in the input image, finding the image closest in color and adding it in.  
Average.jar calculates averages, and mosaic.jar builds the mosaic.
