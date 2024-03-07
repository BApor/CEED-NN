import cv2 as cv

image = cv.imread("0image.jpg")

xtl = 1670
ytl = 424
xbr = 1881
ybr = 601

cut = image[ytl:ybr, xtl:xbr]

cv.imwrite("cut1.jpg", cut)
