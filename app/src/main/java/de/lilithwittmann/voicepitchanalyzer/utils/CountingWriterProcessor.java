package de.lilithwittmann.voicepitchanalyzer.utils;

import java.io.RandomAccessFile;
import java.util.concurrent.atomic.AtomicLong;

import be.tarsos.dsp.AudioEvent;
import be.tarsos.dsp.io.TarsosDSPAudioFormat;
import be.tarsos.dsp.writer.WriterProcessor;

public class CountingWriterProcessor extends WriterProcessor {
    private static final long HEADER_LENGTH = 44;

    private final TarsosDSPAudioFormat format;
    private final AtomicLong fileSize = new AtomicLong(HEADER_LENGTH);

    public CountingWriterProcessor(TarsosDSPAudioFormat format, RandomAccessFile file) {
        super(format, file);
        this.format = format;
    }

    @Override
    public boolean process(AudioEvent audioEvent) {
        fileSize.getAndAdd(audioEvent.getBufferSize() * format.getFrameSize());
        return super.process(audioEvent);
    }

    public long getFileSize() {
        return fileSize.get();
    }
}
