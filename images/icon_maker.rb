require 'rmagick'

include Magick

(1..52).each do |n|
  img = ImageList.new("#{n}.png")
  img[0].crop!(1,2,14,30)
  d = Draw.new
  
  d.line(0,0,13,0)
  d.line(13,29,13,0)
  d.line(13,29,0,29)
  d.line(0,0,0,29)
  d.draw(img)
  img.write("#{n}_icon.png")
end