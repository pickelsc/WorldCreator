package ass2.spec;

public class GameObjectTest extends GameObjectSphere {

	public GameObjectTest(double radius) {
		super(radius);
	}

	@Override
	public void update(long dt) {
		this.setPosition(2.5+MathUtil.sinTable[(int) (dt%7200)/20],2.5+MathUtil.cosTable[(int) (dt%7200)/20]);
	}
}
