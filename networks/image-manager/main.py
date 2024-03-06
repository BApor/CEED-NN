import cv2 as cv

image = cv.imread("../images-labels/primitive/3. Field Pea/images/IMG_4148.JPG")
height, width, _ = image.shape
print("Dimensions: " + str(width) + "x" + str(height))

xtl = 1670
ytl = 424
xbr = 1881
ybr = 601

cut = image[424:601, 1670:1881]
cv.imwrite("cut.jpg", cut)
