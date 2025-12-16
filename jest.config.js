module.exports = {
  collectCoverage: true,
  verbose: true,
  transform: {
    '^.+\\.tsx?$': [
      'ts-jest',
      {
        tsconfig: {
          isolatedModules: true,
          noImplicitAny: false,
        },
      },
    ],
  },
};
