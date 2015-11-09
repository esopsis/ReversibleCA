public class CARule
{
	private static CARule caRule = null;
	private byte[] pattern;
	
	public static CARule getCARule()
	{
		if (caRule == null) caRule = new CARule();
	
		return caRule;
	}
	
	protected CARule()
	{
		pattern = new byte[8];
	}
	
	public void setRule(int rule)
	{
		pattern = new byte[8];
		int r = rule;
		
		for (int i = 0; i<8; i++)
		{
			pattern[i] = (byte)(r % 2);
			
			r = (int)(r / 2);
		}
	}
	
	public byte[] getRule()
	{
		return getCARule().pattern;
	}
}