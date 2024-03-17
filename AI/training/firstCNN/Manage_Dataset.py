from tools.labeledimages import Image_Manager as imman

# imman.process_images_of_type("3. Austrian Winter Pea",
#                              "/Volumes/APORKA SSD/Allamvizsga/Program/CEED-NN/AI/plantseeds",
#                              "/Volumes/APORKA SSD/Allamvizsga/Program/CEED-NN/AI/training/firstCNN/datasets")

imman.resize_to_type_avg("3. Austrian Winter Pea",
                         "/Volumes/APORKA SSD/Allamvizsga/Program/CEED-NN/AI/training/firstCNN/datasets")
