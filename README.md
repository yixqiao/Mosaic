# Mosaic program

## Description
* Create an image mosaic composed of smaller images
* Made using Java (AWT for images)

## Example
![Image example](sample.jpg)
* Image made using faces from CelebA dataset

## Usage (OUTDATED)
1. Clone repository
2. Run `java -jar average.jar -h` to see usage of average generator
3. Run average.jar (example: `java -jar average.jar -n 1000`)
4. Run `java -jar mosaic.jar -h` to see usage of mosaic builder
5. Run mosaic.jar with any input image (example: `java -jar mosaic.jar -p img_in/lake.jpg -o lake_output.jpg`)

## Details
The program works by finding the average color of each of the images to build from. It then goes through chunks in the input image, finding the image closest in color and adding it in.  
Average.jar calculates averages, and mosaic.jar builds the mosaic.
