package flounder.post.filters;

import flounder.engine.*;
import flounder.post.*;
import flounder.resources.*;
import flounder.shaders.*;

public class FilterDOF extends PostFilter {
	private UniformFloat aimDistance = new UniformFloat("aimDistance");
	private UniformFloat nearPlane = new UniformFloat("nearPlane");
	private UniformFloat farPlane = new UniformFloat("farPlane");

	public FilterDOF() {
		super("filterDOF", new MyFile(PostFilter.POST_LOC, "dofFragment.glsl"));
		super.storeUniforms(aimDistance, nearPlane, farPlane);
	}

	@Override
	public void storeValues() {
		aimDistance.loadFloat(FlounderEngine.getCamera().getAimDistance());
		nearPlane.loadFloat(FlounderEngine.getCamera().getNearPlane());
		farPlane.loadFloat(FlounderEngine.getCamera().getFarPlane());
	}
}
