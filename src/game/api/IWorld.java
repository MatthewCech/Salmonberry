package game.api;

public interface IWorld
{
	public void queueEvent(IEvent event);
	public boolean update();
}
