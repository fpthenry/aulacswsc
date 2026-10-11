#!/usr/bin/env node
/**
 * Commit toàn bộ thay đổi theo quy ước Conventional Commits.
 *
 *   npm run commit -- "feat: thêm trang đặt lịch"
 *   npm run commit -- "fix(booking): sửa lỗi lọc theo khách" --push
 *
 * Các bước: kiểm tra message → git add -A → liệt kê file → git commit
 * (husky pre-commit tự chạy prettier qua lint-staged) → git push nếu có --push.
 */
import { spawnSync } from 'node:child_process';

const TYPES = ['feat', 'fix', 'docs', 'style', 'refactor', 'perf', 'test', 'build', 'chore', 'revert'];
const PATTERN = new RegExp(`^(${TYPES.join('|')})(\\([\\w./-]+\\))?!?: .{3,}`);

const args = process.argv.slice(2);
const push = args.includes('--push');
const message = args
  .filter(a => a !== '--push')
  .join(' ')
  .trim();

const git = (gitArgs, opts = {}) => spawnSync('git', gitArgs, { encoding: 'utf8', stdio: opts.capture ? 'pipe' : 'inherit' });
const fail = text => {
  console.error(`\n✖ ${text}\n`);
  process.exit(1);
};

if (!message) {
  fail('Thiếu nội dung commit.\n  Dùng: npm run commit -- "feat: mô tả ngắn" [--push]');
}
if (!PATTERN.test(message)) {
  fail(
    `Message không đúng quy ước: "${message}"\n` +
      `  Dạng đúng: <loại>(<phạm vi>): <mô tả>   vd: "feat(booking): thêm lọc theo ngày"\n` +
      `  <loại> là một trong: ${TYPES.join(', ')}`,
  );
}

if (git(['add', '-A']).status !== 0) fail('git add thất bại.');

const staged = git(['diff', '--cached', '--name-status'], { capture: true }).stdout.trim();
if (!staged) {
  console.log('Không có thay đổi nào để commit.');
  process.exit(0);
}
console.log(`File sẽ commit:\n${staged}\n`);

if (git(['commit', '-m', message]).status !== 0) fail('git commit thất bại (xem lỗi ở trên, thường do prettier/lint-staged).');

if (push) {
  const remotes = git(['remote'], { capture: true }).stdout.trim();
  if (!remotes) fail('Đã commit nhưng chưa push: repo chưa có remote (git remote add origin <url>).');
  const branch = git(['rev-parse', '--abbrev-ref', 'HEAD'], { capture: true }).stdout.trim();
  if (git(['push', '-u', 'origin', branch]).status !== 0) fail('Đã commit nhưng push thất bại.');
}

console.log('\n✔ Xong.');
