package mts.teta.resizer;

import picocli.CommandLine;

import java.io.File;


public class ConsoleAttributes
{

	/*

		Если какой-то из параметров не будет передан, то прога забанит ещё в самом начале  исполнения
		Если не хватит данных для --resize или для --crop
		То прога сама выведет ошибку
	*/

	@CommandLine.Parameters(index="0", description="input-file")
	protected File inputFile = null;

	@CommandLine.Parameters(index="1", description="output-file")
	protected File outputFile = null;

	@CommandLine.Option(names="--resize", arity="2", description="resize the image")
	protected int[] targetReSizes = {-1,-1};

	@CommandLine.Option(names="--quality", arity="1", description="JPEG/PNG compression level")
	protected int targetQuality = -1;

	@CommandLine.Option(names="--crop", arity="4", description="cut out one rectangular area of the image")
	protected int[] targetCropSizes = {-1,-1,-1,-1};

	@CommandLine.Option(names="--blur", arity="1", description="reduce image noise detail levels")
	protected int radius = -1;

	@CommandLine.Option(names="--format", arity="1", description="the image format type")
	protected String formatOfOutput = null;

}
