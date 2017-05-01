package flounder.lwjgl3.devices;

import flounder.devices.*;
import flounder.fbos.*;
import flounder.framework.*;
import flounder.helpers.*;
import flounder.logger.*;
import flounder.platform.*;
import flounder.profiling.*;
import flounder.resources.*;
import org.lwjgl.glfw.*;

import javax.imageio.*;
import java.awt.image.*;
import java.io.*;
import java.nio.*;

import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.EXTTextureFilterAnisotropic.*;
import static org.lwjgl.opengl.GL.*;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.system.MemoryUtil.*;

public class LwjglDisplay extends FlounderDisplay {
	private int windowWidth;
	private int windowHeight;
	private int fullscreenWidth;
	private int fullscreenHeight;

	private String title;
	private MyFile[] icons;
	private boolean vsync;
	private boolean antialiasing;
	private int samples;
	private boolean fullscreen;
	private boolean hiddenDisplay;

	private long window;
	private boolean closed;
	private boolean focused;
	private int windowPosX;
	private int windowPosY;

	private GLFWWindowCloseCallback callbackWindowClose;
	private GLFWWindowFocusCallback callbackWindowFocus;
	private GLFWWindowPosCallback callbackWindowPos;
	private GLFWWindowSizeCallback callbackWindowSize;
	private GLFWFramebufferSizeCallback callbackFramebufferSize;

	public LwjglDisplay() {
		super();
	}

	@Handler.Function(Handler.FLAG_INIT)
	public void init() {
		this.windowWidth = 1080;
		this.windowHeight = 720;
		this.title = "Testing 1";
		this.icons = new MyFile[]{new MyFile(MyFile.RES_FOLDER, "flounder.png")};
		this.vsync = true;
		this.antialiasing = true;
		this.samples = 0;
		this.fullscreen = false;

		// Initialize the GLFW library.
		if (!glfwInit()) {
			FlounderLogger.get().error("Could not init GLFW!");
			System.exit(-1);
		}

		// Gets the video mode from the primary monitor.
		long monitor = glfwGetPrimaryMonitor();
		GLFWVidMode mode = glfwGetVideoMode(monitor);

		// Configures the window.
		glfwDefaultWindowHints();
		glfwWindowHint(GLFW_CONTEXT_VERSION_MAJOR, 3);
		glfwWindowHint(GLFW_CONTEXT_VERSION_MINOR, 0);
		glfwWindowHint(GLFW_VISIBLE, GL_FALSE); // The window will stay hidden until after creation.
		glfwWindowHint(GLFW_RESIZABLE, GL_TRUE); // The window will be resizable depending on if its createDisplay.

		// Use FBO antialiasing instead!
		//if (samples > 0) {
		//	glfwWindowHint(GLFW_SAMPLES, samples); // The number of MSAA samples to use.
		//}

		if (fullscreen && mode != null) {
			fullscreenWidth = mode.width();
			fullscreenHeight = mode.height();

			glfwWindowHint(GLFW_RED_BITS, mode.redBits());
			glfwWindowHint(GLFW_GREEN_BITS, mode.greenBits());
			glfwWindowHint(GLFW_BLUE_BITS, mode.blueBits());
			glfwWindowHint(GLFW_REFRESH_RATE, mode.refreshRate());
		}

		// Create a windowed mode window and its OpenGL context.
		window = glfwCreateWindow(fullscreen ? fullscreenWidth : windowWidth, fullscreen ? fullscreenHeight : windowHeight, title, fullscreen ? monitor : NULL, NULL);

		// Sets the display to fullscreen or windowed.
		focused = true;

		// Gets any window errors.
		if (window == NULL) {
			FlounderLogger.get().error("Could not create the window! Update your graphics drivers and ensure your PC supports OpenGL 3.0!");
			glfwTerminate();
			System.exit(-1);
		}

		// Creates the OpenGL context.
		glfwMakeContextCurrent(window);

		try {
			setWindowIcon();
		} catch (IOException e) {
			FlounderLogger.get().error("Could not load custom display icon!");
			FlounderLogger.get().exception(e);
		}

		// LWJGL will detect the context that is current in the current thread, creates the GLCapabilities instance and makes the OpenGL bindings available for use.
		createCapabilities(true);

		// Gets any OpenGL errors.
		long glError = glGetError();

		if (glError != GL_NO_ERROR) {
			FlounderLogger.get().error("OpenGL Capability Error: " + glError);
			glfwDestroyWindow(window);
			glfwTerminate();
			System.exit(-1);
		}

		// Enables VSync if requested.
		glfwSwapInterval(vsync ? 1 : 0);

		if (vsync) {
			Framework.setFpsLimit(60);
		}

		// Centres the window position.
		if (!fullscreen && mode != null) {
			glfwSetWindowPos(window, (windowPosX = (mode.width() - windowWidth) / 2), (windowPosY = (mode.height() - windowHeight) / 2));
		}

		// Shows the OpenGl window.
		if (hiddenDisplay) {
			glfwHideWindow(window);
		} else {
			glfwShowWindow(window);
		}

		// Sets the displays callbacks.
		glfwSetWindowCloseCallback(window, callbackWindowClose = new GLFWWindowCloseCallback() {
			@Override
			public void invoke(long window) {
				closed = true;
				Framework.requestClose();
			}
		});

		glfwSetWindowFocusCallback(window, callbackWindowFocus = new GLFWWindowFocusCallback() {
			@Override
			public void invoke(long window, boolean focus) {
				focused = focus;
			}
		});

		glfwSetWindowPosCallback(window, callbackWindowPos = new GLFWWindowPosCallback() {
			@Override
			public void invoke(long window, int xpos, int ypos) {
				if (!fullscreen) {
					windowPosX = xpos;
					windowPosY = ypos;
				}
			}
		});

		glfwSetWindowSizeCallback(window, callbackWindowSize = new GLFWWindowSizeCallback() {
			@Override
			public void invoke(long window, int width, int height) {
				if (!fullscreen) {
					windowWidth = width;
					windowHeight = height;
				}
			}
		});

		glfwSetFramebufferSizeCallback(window, callbackFramebufferSize = new GLFWFramebufferSizeCallback() {
			@Override
			public void invoke(long window, int width, int height) {
				glViewport(0, 0, width, height);
			}
		});

		// System logs.
		FlounderLogger.get().log("");
		FlounderLogger.get().log("===== This is not an error message, it is a system info log. =====");
		FlounderLogger.get().log("Flounder Engine Version: " + Framework.getVersion().getVersion());
		FlounderLogger.get().log("Flounder Operating System: " + System.getProperty("os.name"));
		FlounderLogger.get().log("Flounder OpenGL Version: " + glGetString(GL_VERSION));
		FlounderLogger.get().log("Flounder Is OpenGL Modern: " + OpenGlUtils.isModern());
		FlounderLogger.get().log("Flounder OpenGL Vendor: " + glGetString(GL_VENDOR));
		FlounderLogger.get().log("Flounder Available Processors (cores): " + Runtime.getRuntime().availableProcessors());
		FlounderLogger.get().log("Flounder Free Memory (bytes): " + Runtime.getRuntime().freeMemory());
		FlounderLogger.get().log("Flounder Maximum Memory (bytes): " + (Runtime.getRuntime().maxMemory() == Long.MAX_VALUE ? "Unlimited" : Runtime.getRuntime().maxMemory()));
		FlounderLogger.get().log("Flounder Total Memory Available To JVM (bytes): " + Runtime.getRuntime().totalMemory());
		FlounderLogger.get().log("Flounder Maximum FBO Size: " + FBO.getMaxFBOSize());
		FlounderLogger.get().log("Flounder Maximum Anisotropy: " + glGetFloat(GL_MAX_TEXTURE_MAX_ANISOTROPY_EXT));
		FlounderLogger.get().log("===== End of system info log. =====\n");

		super.init();
	}

	private void setWindowIcon() throws IOException {
		// Creates a GLFWImage Buffer,
		GLFWImage.Buffer images = GLFWImage.malloc(icons.length);
		for (int i = 0; i < icons.length; i++) {
			images.put(i, loadGLFWImage(icons[i])); // Stores a image into a slot.
		}

		// Loads the buffer into the window.
		glfwSetWindowIcon(window, images);
	}

	private GLFWImage loadGLFWImage(MyFile file) throws IOException {
		BufferedImage image = ImageIO.read(file.getInputStream());
		int width = image.getWidth();
		int height = image.getHeight();
		int[] pixels = new int[width * height];
		image.getRGB(0, 0, width, height, pixels, 0, width);

		// Converts image to RGBA format.
		ByteBuffer buffer = FlounderPlatform.get().createByteBuffer(width * height * 4);

		for (int y = 0; y < height; y++) {
			for (int x = 0; x < width; x++) {
				int pixel = pixels[y * width + x];
				buffer.put((byte) ((pixel >> 16) & 0xFF)); // Red.
				buffer.put((byte) ((pixel >> 8) & 0xFF)); // Green.
				buffer.put((byte) (pixel & 0xFF)); // Blue.
				buffer.put((byte) ((pixel >> 24) & 0xFF)); // Alpha.
			}
		}

		buffer.flip(); // This will flip the cursor image vertically.

		// Creates a GLFWImage.
		GLFWImage glfwImage = GLFWImage.create();
		glfwImage.width(width); // Setup the images' width.
		glfwImage.height(height); // Setup the images' height.
		glfwImage.pixels(buffer); // Pass image data.
		return glfwImage;
	}

	@Override
	public void swapBuffers() {
		if (this.window != 0) {
			glfwSwapBuffers(this.window);
		}
	}

	@Override
	public BufferedImage getImage(BufferedImage destination, ByteBuffer buffer) {
		// Creates a new destination if it does not exist, or fixes a old one,
		if (destination == null || buffer == null || destination.getWidth() != getWidth() || destination.getHeight() != getHeight()) {
			destination = new BufferedImage(getWidth(), getHeight(), BufferedImage.TYPE_INT_RGB);
			buffer = FlounderPlatform.get().createByteBuffer(getWidth() * getHeight() * 4);
		}

		// Creates a new buffer and stores the displays data into it.
		glReadPixels(0, 0, getWidth(), getHeight(), GL_RGBA, GL_UNSIGNED_BYTE, buffer);

		// Transfers the data from the buffer into the image. This requires bit shifts to get the components data.
		for (int x = destination.getWidth() - 1; x >= 0; x--) {
			for (int y = destination.getHeight() - 1; y >= 0; y--) {
				int i = (x + getWidth() * y) * 4;
				destination.setRGB(x, destination.getHeight() - 1 - y, (((buffer.get(i) & 0xFF) & 0x0ff) << 16) | (((buffer.get(i + 1) & 0xFF) & 0x0ff) << 8) | ((buffer.get(i + 2) & 0xFF) & 0x0ff));
			}
		}

		return destination;
	}

	@Override
	public int getWidth() {
		return this.fullscreen ? this.fullscreenWidth : this.windowWidth;
	}

	@Override
	public int getWindowWidth() {
		return this.windowWidth;
	}

	@Override
	public int getHeight() {
		return this.fullscreen ? this.fullscreenHeight : this.windowHeight;
	}

	@Override
	public int getWindowHeight() {
		return this.windowHeight;
	}

	@Override
	public void setWindowSize(int width, int height) {
		glfwSetWindowSize(this.window, width, height);
		this.windowWidth = width;
		this.windowHeight = height;
	}

	@Override
	public float getAspectRatio() {
		return ((float) getWidth()) / ((float) getHeight());
	}

	@Override
	public String getTitle() {
		return this.title;
	}

	@Override
	public boolean isVSync() {
		return this.vsync;
	}

	@Override
	public void setVSync(boolean vsync) {
		this.vsync = vsync;
		glfwSwapInterval(vsync ? 1 : 0);

		if (vsync) {
			Framework.setFpsLimit(60);
		}
	}

	@Override
	public boolean isAntialiasing() {
		return this.antialiasing;
	}

	@Override
	public void setAntialiasing(boolean antialiasing) {
		this.antialiasing = antialiasing;
	}

	@Override
	public int getSamples() {
		return this.samples;
	}

	@Override
	public void setSamples(int samples) {
		this.samples = samples;
		glfwWindowHint(GLFW_SAMPLES, samples);
	}

	@Override
	public boolean isFullscreen() {
		return this.fullscreen;
	}

	@Override
	public void setFullscreen(boolean fullscreen) {
		if (this.fullscreen == fullscreen) {
			return;
		}

		this.fullscreen = fullscreen;
		long monitor = glfwGetPrimaryMonitor();
		GLFWVidMode mode = glfwGetVideoMode(monitor);

		FlounderLogger.get().log(fullscreen ? "Display is going fullscreen." : "Display is going windowed.");

		if (fullscreen) {
			this.fullscreenWidth = mode.width();
			this.fullscreenHeight = mode.height();
			glfwSetWindowMonitor(this.window, monitor, 0, 0, this.fullscreenWidth, this.fullscreenHeight, GLFW_DONT_CARE);
		} else {
			this.windowPosX = (mode.width() - this.windowWidth) / 2;
			this.windowPosY = (mode.height() - this.windowHeight) / 2;
			glfwSetWindowMonitor(this.window, NULL, this.windowPosX, this.windowPosY, this.windowWidth, this.windowHeight, GLFW_DONT_CARE);
		}
	}

	@Override
	public long getWindow() {
		return this.window;
	}

	@Override
	public boolean isClosed() {
		return this.closed;
	}

	@Override
	public boolean isFocused() {
		return this.focused;
	}

	@Override
	public int getWindowXPos() {
		return this.windowPosX;
	}

	@Override
	public int getWindowYPos() {
		return this.windowPosY;
	}

	@Handler.Function(Handler.FLAG_UPDATE_PRE)
	public void update() {
		super.update();
	}

	@Handler.Function(Handler.FLAG_PROFILE)
	public void profile() {
		super.profile();

		FlounderProfiler.get().add(getTab(), "Width", windowWidth);
		FlounderProfiler.get().add(getTab(), "Height", windowHeight);
		FlounderProfiler.get().add(getTab(), "Title", title);
		FlounderProfiler.get().add(getTab(), "VSync", vsync);
		FlounderProfiler.get().add(getTab(), "Antialiasing", antialiasing);
		FlounderProfiler.get().add(getTab(), "Samples", samples);
		FlounderProfiler.get().add(getTab(), "Fullscreen", fullscreen);

		FlounderProfiler.get().add(getTab(), "Closed", closed);
		FlounderProfiler.get().add(getTab(), "Focused", focused);
		FlounderProfiler.get().add(getTab(), "Window Pos.X", windowPosX);
		FlounderProfiler.get().add(getTab(), "Window Pos.Y", windowPosY);
	}

	@Handler.Function(Handler.FLAG_DISPOSE)
	public void dispose() {
		super.dispose();

		callbackWindowClose.free();
		callbackWindowFocus.free();
		callbackWindowPos.free();
		callbackWindowSize.free();
		callbackFramebufferSize.free();

		destroy();
		glfwDestroyWindow(window);
		glfwTerminate();

		this.closed = false;
	}
}