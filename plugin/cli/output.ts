const loaderSymbols = ['⠇', '⠏', '⠷', '⠧', '⠼', '⠴', '⠦', '⠇'];

export class Loader {
  public static shared = new Loader();
  private interval: NodeJS.Timeout | null = null;
  private index = 0;

  public start(message: string) {
    if (this.interval) {
      clearInterval(this.interval);
      this.interval = null;
      process.stdout.write('\r');
    }

    process.stdout.write('\x1B[?25l');

    this.index = 0;
    this.interval = setInterval(() => {
      process.stdout.write(`\r${loaderSymbols[this.index]} ${message}`);
      this.index = (this.index + 1) % loaderSymbols.length;
    }, 100);
  }

  public stop() {
    if (this.interval) {
      clearInterval(this.interval);
      this.interval = null;
    }

    process.stdout.write('\r');
    process.stdout.write('\x1B[?25h');
  }
}
