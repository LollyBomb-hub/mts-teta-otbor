package mts.teta.resizer.imageprocessor;

import mts.teta.resizer.imageprocessor.BadAttributesException;
import mts.teta.resizer.ResizerApp;

import net.coobird.thumbnailator.Thumbnails; // For resize, quality

import marvin.image.*;
import marvin.io.*;
import static marvinplugins.MarvinPluginCollection.*;

import java.io.File;
import javax.imageio.IIOException;
import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;

public class ImageProcessor
{

	private void out(int v)
	{
		System.out.println(v);
		return;
	}

	/*
		Накой пердают image и при этом в тесты впихнули сеттер не в курсе, но надо значит надо
		Программа умеет:
			* Менять размер фотографии
			* Вырезать такой-то фрагмент(операция crop)
			* Блюрить изображение(операция blur)
		Если каждый из аргументов указан по отдельности, то есть только 1 какой-то функционал,
		то только этот функционал при условии валидности и будет указан
		Если комбинация указана, то я лично буду делать следующее:
			Сначала меняю размер(Если есть этот пункт)
			Затем вырезаю кусок(из предыдущего результата)
			Затем в "выхлоп" предыдущей команды добавлю blur
	*/

	private boolean[] flags = {true, true, true, true, true};

	public void processImage(ResizerApp app) throws Exception
	{
		int quality = app.getQuality();

		if(quality > 100 || quality < 1)
			flags[0] = false;

		int[] reSizes = app.getTargetReSizes();

		if(reSizes[0] <= 0 || reSizes[1] <= 0)
			flags[1] = false;

		int[] cropSizes = app.getTargetCropSizes();

		if(cropSizes[0] < 1 || cropSizes[1] < 1 || cropSizes[2] < 1 || cropSizes[3] < 1)
			flags[2] = false;

		int blurRadius = app.getBlurRadius();

		if(blurRadius < 0)
			flags[3] = false;

		String format = app.getOutputFormat();

		if(format == null)
			flags[4] = false;

		File inputFile = app.getInputFile(); // Вход и выход
		File outputFile = app.getOutputFile();

		if(!inputFile.exists()) // Опять не обманешь
			throw new IIOException("Can't read input file!");

		try
		{

			Thumbnails.of(inputFile).scale(1.).toFile(outputFile);

			if(flags[1]) // if resizes are set
                                Thumbnails.of(outputFile).forceSize(reSizes[0], reSizes[1]).toFile(outputFile);

			if(flags[0]) // if quality is set
				Thumbnails.of(outputFile).scale(1.).outputQuality(quality / 100.).toFile(outputFile);

			if(flags[4]) // output format
				Thumbnails.of(outputFile).scale(1.).outputFormat(format).toFile(outputFile);

			MarvinImage image = MarvinImageIO.loadImage(outputFile.getAbsolutePath());

			if(flags[2])
				crop(image.clone(), image, cropSizes[2], cropSizes[3], cropSizes[0], cropSizes[1]);

			MarvinImageIO.saveImage(image, outputFile.getAbsolutePath());

			if(flags[3]) // Это самая медленная штука тут
			{
				// Буду блюрить уменьшенную копию, а затем восстанавливать размер
				MarvinImageIO.saveImage(image, outputFile.getAbsolutePath());
				BufferedImage buffer = ImageIO.read(outputFile);
				int width = buffer.getWidth();
				int height = buffer.getHeight();
				Thumbnails.of(outputFile).scale(0.2).toFile(outputFile);
				image = MarvinImageIO.loadImage(outputFile.getAbsolutePath());
				gaussianBlur(image.clone(), image, blurRadius);
				MarvinImageIO.saveImage(image, outputFile.getAbsolutePath());
				Thumbnails.of(outputFile).forceSize(width, height).toFile(outputFile);
			}

		}
		catch(NullPointerException e)
		{
			// Опять скорее всего с файлом проблема, но это не точно, а доки упоминали такую ошибку
			System.out.println(e.getMessage());
			throw new BadAttributesException("Please check params!");
		}
		catch(IllegalArgumentException e)
		{
			// Функции вернули ошибку с данными, которую я походу проглядел, маловероятно, но чем чёрт не шутит
			System.out.println(e.getMessage());
			throw new BadAttributesException("Please check params!");
		}
		catch(IIOException e)
		{
			System.out.println(e.getMessage());
			throw new IIOException("IO operation error!");
		}
		return;
	}

}
