package mts.teta.resizer;

import mts.teta.resizer.imageprocessor.ImageProcessor;

import picocli.CommandLine;

import java.io.File;
import javax.imageio.ImageIO;
import java.util.concurrent.Callable;


@CommandLine.Command(name="resizer", mixinStandardHelpOptions=true, version="0.0.1b", description="Available formats: jpeg png")
public class ResizerApp extends ConsoleAttributes implements Callable<Integer>
{

	public static void main(String... args)
	{
		int exitCode = runConsole(args);
		System.exit(exitCode);
	}

	protected static int runConsole(String[] args)
	{
		return new CommandLine(new ResizerApp()).execute(args);
	}

	public void setInputFile(File file)
	{
		this.inputFile = file; // Устанавливаем входной файл
		return;
	}

	public File getInputFile()
	{
		return this.inputFile; // Геттер для входного файла
	}

	public void setOutputFile(File file)
	{
		this.outputFile = file; // Сеттер выходного файла
		return;
	}

	public File getOutputFile()
	{
		return this.outputFile; // Геттер выходного файла
	}

	public void setResizeWidth(int width)
	{
		this.targetReSizes[0] = width; // Сеттер ширины
		return;
	}

	public void setResizeHeight(int height)
	{
		this.targetReSizes[1] = height; // Сеттер высоты
		return;
	}

	public int[] getTargetReSizes()
	{
		return this.targetReSizes; // Геттер высоты
	}

	public void setQuality(int quality)
	{
		this.targetQuality = quality; // Сеттер качества
		return;
	}

	public int getQuality()
	{
		return this.targetQuality; // Геттер качества
	}

	public int[] getTargetCropSizes()
	{
		return this.targetCropSizes;
	}

	public int getBlurRadius()
	{
		return this.radius;
	}

	public String getOutputFormat()
	{
		return this.formatOfOutput;
	}

	@Override
	public Integer call() throws Exception
	{
		ImageProcessor imageProcessor = new ImageProcessor();
		imageProcessor.processImage(this);
		return 0;
	}

}

