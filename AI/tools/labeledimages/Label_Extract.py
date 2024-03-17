import cv2 as cv
import os

# This program extracts the label from xml files

try:
    with open("/plantseeds/3. Austrian Winter Pea/labels/annotations.xml",
              "r") as file:
        read_xml_file = file.read()
except FileNotFoundError:
    print("read file error")
    exit(1)

write_xml_file = open("/Volumes/APORKA SSD/Allamvizsga/Program/CEED-NN/AI/plantseeds/3. Austrian Winter Pea/labels/onlylabels.xml",
                      "w")


for row in read_xml_file.split('\n'):
    if ((row.strip().lower().startswith('<box') or row.strip().lower().startswith('</box>'))
       or (row.strip().lower().startswith('<image') or row.strip().lower().startswith('</image>'))):
        write_xml_file.write(row + "\n")

write_xml_file.close()

print("Label extraction complete!")