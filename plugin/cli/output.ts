import chalk from 'chalk';

export const bulletSymbol = '•';
export const checkmarkSymbol = '✔';
export const crossSymbol = '✖';
export const warningSymbol = '⚠';
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

    process.stdout.clearLine(0);
    process.stdout.cursorTo(0);
    // process.stdout.write('\r');
    process.stdout.write('\x1B[?25h');
  }
}

export const errorMessage = (message: string) => {
  console.error(`${chalk.red(crossSymbol)} ${message}`);
};

export const infoMessage = (message: string) => {
  console.info(`${chalk.blueBright(bulletSymbol)} ${message}`);
};

export const successMessage = (message: string) => {
  console.info(`${chalk.green(checkmarkSymbol)} ${message}`);
};

export const warningMessage = (message: string) => {
  console.warn(`${chalk.yellow(warningSymbol)} ${message}`);
};
