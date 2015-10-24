package ass2.spec;

public class GameObjectTest extends GameObjectSphere {

	public GameObjectTest(String texture, String ext, boolean ground) {
		super(texture, ext, ground);
	}

	@Override
	public void update(long dt) {
		this.setPosition(2.5+MathUtil.sinTable[(int) (dt%7200)/20],2.5+MathUtil.cosTable[(int) (dt%7200)/20]);
	}
}
