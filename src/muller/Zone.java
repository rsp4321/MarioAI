package muller;
/**
 * A zone
 * contains the actual matrix, plus some information about the matrix
 * @author mis09mmh
 */
public class Zone {
	int width;
	int height;
	
	int nofuncs;
	int noPowerups;
	int noCoins;
	int noTubes;
	int noPlattforms;
	int noGaps;
	int noBlocks;
        int noObject;
        int noObjwithoutBlocks;
	
	int id;
	String functions[];
	int matrix[][];

	
	public Zone() {
		nofuncs=0;
		noPowerups=0;
		noCoins=0;
		noTubes=0;
		noPlattforms=0;
		noGaps=0;
		noBlocks=0;
                noObject=0;
                noObjwithoutBlocks=0;
	}
	
	public int getElement(int x, int y) {
		return matrix[x][y];
	}
	public void print() {
		System.out.println("functions:" + nofuncs);
		for (int i = 0; i < nofuncs; i++) {
		//	System.out.print(functions[i]+"\n");
		}
		System.out.print("noPowerups:"+noPowerups+"\n");
		System.out.print("noCoins:"+noCoins+"\n");
		System.out.print("noTubes:"+noTubes+"\n");
		System.out.print("noPlattforms:"+noPlattforms+"\n");
		System.out.print("noGaps:"+noGaps+"\n");
		System.out.print("noBlocks:"+noBlocks+"\n");


		System.out.println("width:" + width);
		System.out.println("height:" + height);
                System.out.println("matrixwidth="+matrix[0].length);
                System.out.println("matrixheight="+matrix.length);
		for (int j = 0; j < height; j++) {
			for (int i = 0; i < width; i++) {

				System.out.print(matrix[j][i] + " ");
			}
			System.out.print("\n");
		}
	}
	public int getWidth() {
		return width;
	}
	public void setWidth(int width) {
		this.width = width;
	}
	public int getHeight() {
		return height;
	}
	public void setHeight(int height) {
		this.height = height;
	}
	public int getNofuncs() {
		return nofuncs;
	}
	public void setNofuncs(int nofuncs) {
		this.nofuncs = nofuncs;
	}
	public int getNoPowerups() {
		return noPowerups;
	}
	public void setNoPowerups(int noPowerups) {
		this.noPowerups = noPowerups;
	}
	public int getNoCoins() {
		return noCoins;
	}
	public void setNoCoins(int noCoins) {
		this.noCoins = noCoins;
	}
	public int getNoTubes() {
		return noTubes;
	}
	public void setNoTubes(int noTubes) {
		this.noTubes = noTubes;
	}
	public int getNoPlattforms() {
		return noPlattforms;
	}
	public void setNoPlattforms(int noPlattforms) {
		this.noPlattforms = noPlattforms;
	}
	public int getNoGaps() {
		return noGaps;
	}
	public void setNoGaps(int noGaps) {
		this.noGaps = noGaps;
	}
	public int getNoBlocks() {
		return noBlocks;
	}
	public void setNoBlocks(int noBlocks) {
		this.noBlocks = noBlocks;
	}
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public String[] getFunctions() {
		return functions;
	}
	public void setFunctions(String[] functions) {
		this.functions = functions;
	}
	public int[][] getMatrix() {
		return matrix;
	}
	public void setMatrix(int[][] matrix) {
		this.matrix = matrix;
	}
}
