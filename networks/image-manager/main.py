import cv2 as cv

image = cv.imread("../datasets/primitive/3. Field Pea/images/IMG_4148.JPG")
height, width, _ = image.shape
print(str(width) + "x" + str(height))



cut = image[0:1000, 0:1000]
cv.imwrite("cut.jpg", cut)
