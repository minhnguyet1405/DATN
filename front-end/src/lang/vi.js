export default {
  common: {
    back: 'Quay lại',
    return: 'Trở về'
  },
  route: {
    logIn: 'Đăng nhập',
    dashboard: 'Tổng quan',
    introduction: 'Giới thiệu',
    documentation: 'Tài liệu',
    guide: 'Hướng dẫn',
    permission: 'Quyền truy cập',
    pagePermission: 'Trang giới hạn truy cập',
    directivePermission: 'Thuộc tính truy cập',
    icons: 'Ký hiệu',
    components: 'Thành phần',
    componentIndex: 'Giới thiệu',
    tinymce: 'Tinymce',
    markdown: 'Markdown',
    jsonEditor: 'Soạn thảo JSON',
    dndList: 'Danh sách Dnd',
    splitPane: 'SplitPane',
    avatarUpload: 'Tải hình đại diện',
    dropzone: 'Dropzone',
    sticky: 'Sticky',
    countTo: 'CountTo',
    componentMixin: 'Mixin',
    backToTop: 'BackToTop',
    dragDialog: 'Drag Dialog',
    dragSelect: 'Drag Select',
    dragKanban: 'Drag Kanban',
    charts: 'Biểu đồ',
    keyboardChart: 'Biểu đồ bàn phím',
    lineChart: 'Biểu đồ đường',
    mixChart: 'Biểu đồ đa dạng',
    example: 'Ví dụ',
    nested: 'Danh mục con',
    menu1: 'Danh mục 1',
    'menu1-1': 'Danh mục 1-1',
    'menu1-2': 'Danh mục 1-2',
    'menu1-2-1': 'Danh mục 1-2-1',
    'menu1-2-2': 'Danh mục 1-2-2',
    'menu1-3': 'Danh mục 1-3',
    menu2: 'Danh mục 2',
    Table: 'Bảng',
    dynamicTable: 'Bảng động',
    dragTable: 'Bảng kéo thả',
    inlineEditTable: 'Chỉnh sửa trực tiếp',
    complexTable: 'Bảng phức tạp',
    treeTable: 'Bảng dạng cây',
    customTreeTable: 'Bảng dạng cây tùy biến',
    tab: 'Tab',
    form: 'Form',
    createArticle: 'Viết bài',
    editArticle: 'Chỉnh sửa',
    articleList: 'Tin tức',
    errorPages: 'Trang lỗi',
    page401: '401',
    page404: '404',
    errorLog: 'Lỗi',
    excel: 'Excel',
    exportExcel: 'Export Excel',
    selectExcel: 'Export Selected',
    uploadExcel: 'Upload Excel',
    zip: 'Zip',
    exportZip: 'Export Zip',
    theme: 'Theme',
    clipboardDemo: 'Clipboard',
    i18n: 'I18n',
    externalLink: 'Liên kết ngoài',
    elementUi: 'Thành phần giao diện',
    administrator: 'Quản trị',
    users: 'Tài khoản',
    userProfile: 'Tài khoản người dùng',
    rolePermission: 'Quản lý vai trò',
    unitManage: 'Quản lý đơn vị',
    roleManage: 'Quản lý vai trò',
    userManage: 'Quản lý người dùng',
    videoPage: 'Videos',
    videoList: 'DS Video'
  },
  navbar: {
    logOut: 'Đăng xuất',
    dashboard: 'Tổng quan',
    github: 'Github',
    screenfull: 'Toàn màn hình',
    theme: 'Theme',
    size: 'Global Size',
    profile: 'Trang cá nhân'
  },
  login: {
    title: 'ITS Cao tốc',
    logIn: 'Đăng nhập',
    username: 'Username',
    email: 'Email',
    password: 'Mật khẩu',
    any: 'any',
    thirdparty: 'Hoặc đăng nhập với',
    thirdpartyTips:
      'Không thể giả lập, xi vui lòng áp dụng tùy theo tình huống nghiệp vụ của bạn! ! !',
    button: 'Đăng nhập'
  },
  documentation: {
    documentation: 'Tài liệu',
    github: 'Github Repository',
    laravel: 'Laravel'
  },
  permission: {
    roles: 'Nhóm của bạn',
    switchRoles: 'Đổi nhóm',
    tips: 'Trong vài trường hợp sẽ không thích hợp để dùng v-permission, như là thành phần Tab hay el-table-column và các trường hợp render DOM không đồng bộ, khi đó chỉ có thể xử lý bằng tay với v-if.',
    editPermission: 'Sửa quyền',
    cancel: 'Hủy',
    confirm: 'Đồng ý'
  },
  guide: {
    description:
      'Trang hướng dẫn sẽ có ích cho những người mới vào website lần đầu. Bạn có thể giới thiệu sơ lược các chức năng của website. Kiểm tra demo',
    button: 'Hiện hướng dẫn'
  },
  components: {
    documentation: 'Tài liệu',
    tinymceTips:
      'Rich text editor is a core part of management system, but at the same time is a place with lots of problems. In the process of selecting rich texts, I also walked a lot of detours. The common rich text editors in the market are basically used, and the finally chose Tinymce. See documentation for more detailed rich text editor comparisons and introductions.',
    dropzoneTips:
      'Because my business has special needs, and has to upload images to qiniu, so instead of a third party, I chose encapsulate it by myself. It is very simple, you can see the detail code in @/components/Dropzone.',
    stickyTips:
      'When the page is scrolled to the preset position will be sticky on the top.',
    backToTopTips1:
      'When the page is scrolled to the specified position, the Back to Top button appears in the lower right corner',
    backToTopTips2:
      'You can customize the style of the button, show / hide, height of appearance, height of the return. If you need a text prompt, you can use element-ui el-tooltip elements externally',
    imageUploadTips:
      'Since I was using only the vue@1 version, and it is not compatible with mockjs at the moment, I modified it myself, and if you are going to use it, it is better to use official version.'
  },
  table: {
    description: 'Mô tả',
    dynamicTips1: 'Fixed header, sorted by header order',
    dynamicTips2: 'Not fixed header, sorted by click order',
    dragTips1: 'The default order',
    dragTips2: 'The after dragging order',
    name: 'Tên',
    title: 'Tiêu đề',
    importance: 'IMP',
    type: 'Thể loại',
    remark: 'Remark',
    search: 'Tìm kiếm',
    add: 'Thêm',
    export: 'Export',
    reviewer: 'reviewer',
    id: 'ID',
    date: 'Ngày',
    author: 'Tác giả',
    readings: 'Số lần đọc',
    status: 'Status',
    actions: 'Actions',
    edit: 'Edit',
    publish: 'Publish',
    draft: 'Draft',
    delete: 'Xóa',
    cancel: 'Hủy bỏ',
    confirm: 'Đồng ý',
    keyword: 'Từ khóa',
    role: 'Nhóm'
  },
  errorLog: {
    tips: 'Please click the bug icon in the upper right corner',
    description:
      'Now the management system are basically the form of the spa, it enhances the user experience, but it also increases the possibility of page problems, a small negligence may lead to the entire page deadlock. Fortunately Vue provides a way to catch handling exceptions, where you can handle errors or report exceptions.',
    documentation: 'Document introduction'
  },
  excel: {
    export: 'Export',
    selectedExport: 'Export Selected Items',
    placeholder: 'Please enter the file name(default excel-list)'
  },
  zip: {
    export: 'Export',
    placeholder: 'Please enter the file name(default file)'
  },
  pdf: {
    tips: 'Here we use window.print() to implement the feature of downloading pdf.'
  },
  theme: {
    change: 'Change Theme',
    documentation: 'Theme documentation',
    tips: 'Tips: It is different from the theme-pick on the navbar is two different skinning methods, each with different application scenarios. Refer to the documentation for details.'
  },
  tagsView: {
    refresh: 'Refresh',
    close: 'Close',
    closeOthers: 'Close Others',
    closeAll: 'Close All'
  },
  user: {
    role: 'Nhóm',
    password: 'Mật khẩu',
    confirmPassword: 'Nhập lại mật khẩu',
    name: 'Tên',
    email: 'Địa chỉ email'
  },
  roles: {
    description: {
      admin:
        'Super Administrator. Have access and full permission to all pages.',
      manager:
        'Manager. Have access and permission to most of pages except permission page.',
      editor:
        'Editor. Have access to most of pages, full permission with articles and related resources.',
      user: 'Normal user. Have access to some pages',
      visitor:
        'Visitor. Have access to static pages, should not have any writable permission'
    }
  }
}
