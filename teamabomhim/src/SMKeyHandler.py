from panda3d.core import KeyboardButton, Point2

# Key list here: https://www.panda3d.org/dox/cxx/html/class_keyboard_button.html

class SMKeyHandler():
	
	def poll(self, keyChar):
		keyPressed = base.mouseWatcherNode.is_button_down
		keyExp = "KeyboardButton."
		if(len(keyChar) > 1):
			keyExp += (str(keyChar) + "()")
		else:
			keyExp += ("ascii_key('" + str(keyChar.lower()[:1]) + "')")
		key = eval(keyExp)
		if keyPressed(key):
			return True
		else:
			return False

	def getMouse(self):
		if base.mouseWatcherNode.hasMouse():
			x = base.mouseWatcherNode.getMouseX()
			y = base.mouseWatcherNode.getMouseY()
		else:
			x = 0
			y = 0
		return Point2(x, y)
		
