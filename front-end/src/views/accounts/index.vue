<!-- eslint-disable vue/valid-v-model -->
<template>
  <div class="body">
    <div>
      <el-input
        v-model="queryPage.search"
        class="input-search"
        placeholder="Tìm kiếm"
        prefix-icon="el-icon-search"
        @change="onChangeInputSearch"
      />
      <el-button
        class="btn-add"
        type="primary"
        icon="el-icon-plus"
        @click="handleCreate()"
      >Thêm mới</el-button>
    </div>
    <div class="mb-4">
      <div id="vehicle-table">
        <!-- <div class="table-responsive"> -->
        <div v-if="multiSelected.length > 0" id="select-all-delete">
          <i class="el-icon-close" @click="closeDelete()" />
          <span
            v-if="multiSelected.length < 10"
          >0{{ multiSelected.length }}</span><span v-else>{{ multipleSelection.length }}</span> mục được chọn
          <el-button
            type="danger"
            :loading="loading_delete_all"
            @click="destroyMulti()"
          ><i class="el-icon-delete" /> Xóa</el-button>
        </div>
        <el-table
          ref="multipleTable"
          v-loading="loading"
          :data="userList"
          style="width: 100%"
          @selection-change="handleSelectionChange"
          @row-click="rowClicked"
        >
          <el-table-column type="selection" width="50" />
          <el-table-column label="STT" width="50">
            <template slot-scope="scope">{{
              scope.$index +
                1 +
                (queryPage.page > 0 ? queryPage.page - 1 : 0) * queryPage.size
            }}</template>
          </el-table-column>
          <el-table-column prop="fullName" label="Họ và tên" />
          <el-table-column prop="username" label="Tên đăng nhập" />
          <el-table-column id="gender" label="Giới tính">
            <template slot-scope="scope">{{
              scope.row.gender === 0 ? "Nam" : "Nữ"
            }}</template>
          </el-table-column>
          <el-table-column label="Ngày sinh">
            <template slot-scope="scope">{{
              scope.row.birthday | formatDatetime
            }}</template>
          </el-table-column>
          <el-table-column prop="phoneNumber" label="Số điện thoại" />
        </el-table>
        <el-pagination
          :current-page.sync="queryPage.page"
          :page-sizes="[1, 5, 10, 15, 20]"
          :page-size="queryPage.size"
          layout="total, sizes, prev, pager, next, jumper"
          :total="queryPage.total"
          @size-change="handleSizeChange"
          @current-change="handleCurrentChange"
        />
      </div>
      <div id="detail-vehicle" class="violation-ruleForm" style="width: 0">
        <i class="el-icon-close" @click="closeDetail()" />
        <template v-if="userDetail">
          <div class="avatar">
            <img
              v-if="userDetail.avatar"
              height="125px"
              width="125px"
              :src="userDetail.avatar"
            >
            <img v-else height="125px" width="125px" :src="user_default">
          </div>

          <template v-if="userDetail.username">
            <div class="item-label">Tên đăng nhập</div>
            <div class="item-text">{{ userDetail.username }}</div>
          </template>

          <template v-if="userDetail.fullName">
            <div class="item-label">Họ và tên</div>
            <div class="item-text">
              {{ userDetail.fullName }}
            </div>
          </template>

          <template v-if="userDetail.birthday">
            <div class="item-label">Ngày sinh</div>
            <div class="item-text">
              {{ userDetail.birthday | formatDatetime }}
            </div>
          </template>

          <template v-if="userDetail.phoneNumber">
            <div class="item-label">Số điện thoại</div>
            <div class="item-text">{{ userDetail.phoneNumber }}</div>
          </template>

          <template v-if="userDetail.email">
            <div class="item-label">Email</div>
            <div class="item-text">{{ userDetail.email }}</div>
          </template>

          <div class="action">
            <el-button
              type="info"
              @click="handleEdit(userDetail)"
            ><i class="el-icon-edit" /> Sửa
            </el-button>
            <el-button
              type="danger"
              style="float: right"
              :loading="loadingVehicle"
              @click="destroy(userDetail)"
            ><i class="el-icon-delete" /> Xóa
            </el-button>
          </div>
        </template>
      </div>
    </div>

    <el-dialog
      title="Thêm mới"
      :visible.sync="dialogAdd"
      width="700px"
      :close-on-click-modal="false"
      @close="closeDialog('addForm')"
    >
      <el-form
        ref="addForm"
        :model="userInfo"
        :rules="rules"
        label-position="top"
        label-width="100%"
      >
        <div class="block-item">
          <div class="item-left">
            <el-form-item

              label="Họ và tên"
              prop="fullName"
            >
              <el-input
                v-model="userInfo.fullName"
                placeholder="Nhập họ và tên"
              />
            </el-form-item>

            <el-form-item

              label="Tên đăng nhập"
              prop="username"
            >
              <el-input
                v-model.trim="userInfo.username"
                placeholder="Nhập tên đăng nhập"
                @copy.native.prevent
                @paste.native.prevent
                @click.native.right.prevent
              />
            </el-form-item>

            <el-form-item

              label="Mật khẩu"
              prop="password"
            >
              <el-input
                v-model="userInfo.password"
                type="password"
                placeholder="Nhập mật khẩu"
              />
            </el-form-item>
          </div>
          <div class="item-right">
            <el-form-item>
              <div class="img_avatar" label="Avatar" prop="avatar">
                <img
                  v-if="url"
                  class="image"
                  height="200px"
                  width="200px"
                  :src="url"
                  :alt="url"
                  @click="handleClick"
                >
                <img
                  v-else
                  class="image"
                  height="200px"
                  width="200px"
                  :src="user_default"
                  :alt="user_default"
                  @click="handleClick"
                >
                <div class="img_icon">
                  <input
                    v-if="uploadReady"
                    ref="file"
                    type="file"
                    hidden
                    @change="fileSelected"
                  >
                  <div class="avatar-uploader">
                    <i
                      class="el-icon-camera-solid avatar-uploader-icon"
                      @click="handleClick"
                    />
                  </div>
                </div>
              </div>
            </el-form-item>
          </div>
        </div>
        <div class="block-item">
          <div class="item-left">
            <el-form-item

              label="Nhập lại mật khẩu"
              prop="matchingPassword"
            >
              <el-input
                v-model="userInfo.matchingPassword"
                type="password"
                placeholder="Nhập lại mật khẩu"
                @copy.native.prevent
                @paste.native.prevent
                @click.native.right.prevent
              />
            </el-form-item>

            <el-form-item

              label="Ngày sinh"
              prop="birthday"
            >
              <el-date-picker
                v-model="userInfo.birthday"
                type="date"
                :picker-options="datePickerOptions"
                placeholder="Ngày sinh"
                format="dd/MM/yyyy"
                style="width: 100%"
              />
            </el-form-item>

            <el-form-item

              label="Giới tính"
              prop="gender"
            >
              <el-radio-group v-model="userInfo.gender">
                <el-radio :label="0">Nam</el-radio>
                <el-radio :label="1">Nữ</el-radio>
              </el-radio-group>
            </el-form-item>
            <el-form-item
              label="Phòng ban"
              prop="department"
            >
              <el-select
                v-model="userInfo.department"
                style="width: 100%"
                filterable
                placeholder="Vui lòng chọn"
              >
                <el-option
                  v-for="department in departmentList"
                  :key="department.id"
                  :label="department.name"
                  :value="department.id"
                />
              </el-select>
            </el-form-item>
          </div>
          <div class="item-right">
            <el-form-item

              label="Số điện thoại"
              prop="phoneNumber"
            >
              <el-input
                v-model="userInfo.phoneNumber"
                placeholder="Nhập số điện thoại"
              />
            </el-form-item>

            <el-form-item

              label="Email"
              prop="email"
            >
              <el-input v-model="userInfo.email" placeholder="Nhập email" />
            </el-form-item>

            <el-form-item label="Quyền" prop="role">
              <el-select
                v-model="userInfo.role"
                filterable
                placeholder="Chọn quyền"
                style="width: 100%"
              >
                <el-option
                  v-for="role in rolesLst"
                  :key="role.value"
                  :label="role.label"
                  :value="role.value"
                />
              </el-select>
            </el-form-item>
            <el-form-item

              label="Vị trí"
              prop="position"
            >
              <el-input
                v-model="userInfo.position"
                placeholder="Java,C#,C++..."
              />
            </el-form-item>
          </div>
        </div>
      </el-form>

      <div slot="footer">
        <el-button
          class="cancel-btn"
          type="info"
          @click="dialogAdd = false"
        >Hủy</el-button>
        <el-button
          type="primary"
          :loading="loading_add"
          @click="addUser()"
        >Lưu
        </el-button>
      </div>
    </el-dialog>

    <el-dialog
      title="Cập nhật"
      :visible.sync="dialogEdit"
      width="700px"
      :close-on-click-modal="false"
      @close="closeDialog('editForm')"
    >
      <el-form
        ref="editForm"
        :model="userInfo"
        :rules="rulesEdit"
        label-position="top"
        label-width="100%"
      >
        <div class="block-item">
          <div class="item-left">
            <el-form-item

              label="Họ và tên"
              prop="fullName"
            >
              <el-input
                v-model="userInfo.fullName"
                placeholder="Nhập họ và tên"
              />
            </el-form-item>

            <el-form-item

              label="Tên đăng nhập"
              prop="username"
            >
              <el-input
                v-model.trim="userInfo.username"
                disabled
                @copy.native.prevent
                @paste.native.prevent
                @click.native.right.prevent
              />
            </el-form-item>

            <el-form-item

              label="Mật khẩu"
              prop="password"
            >
              <el-input
                v-model="userInfo.password"
                type="password"
                placeholder="Nhập mật khẩu"
              />
            </el-form-item>
          </div>
          <div class="item-right">
            <el-form-item>
              <div class="img_avatar" label="Avatar" prop="avatar">
                <img
                  v-if="url"
                  class="image"
                  height="200px"
                  width="200px"
                  :src="url"
                  :alt="url"
                  @click="handleClick"
                >
                <img
                  v-else
                  class="image"
                  height="200px"
                  width="200px"
                  :src="user_default"
                  :alt="user_default"
                  @click="handleClick"
                >
                <div class="img_icon">
                  <input
                    v-if="uploadReady"
                    ref="file"
                    type="file"
                    hidden
                    @change="fileSelected"
                  >
                  <div class="avatar-uploader">
                    <i
                      class="el-icon-camera-solid avatar-uploader-icon"
                      @click="handleClick"
                    />
                  </div>
                </div>
              </div>
            </el-form-item>
          </div>
        </div>
        <div class="block-item">
          <div class="item-left">
            <el-form-item

              label="Nhập lại mật khẩu"
              prop="matchingPassword"
            >
              <el-input
                v-model="userInfo.matchingPassword"
                type="password"
                placeholder="Nhập lại mật khẩu"
                @copy.native.prevent
                @paste.native.prevent
                @click.native.right.prevent
              />
            </el-form-item>

            <el-form-item

              label="Ngày sinh"
              prop="birthday"
            >
              <el-date-picker
                v-model="userInfo.birthday"
                type="date"
                :picker-options="datePickerOptions"
                placeholder="Ngày sinh"
                format="dd/MM/yyyy"
                style="width: 100%"
              />
            </el-form-item>

            <el-form-item

              label="Giới tính"
              prop="gender"
            >
              <el-radio-group v-model="userInfo.gender">
                <el-radio :label="0">Nam</el-radio>
                <el-radio :label="1">Nữ</el-radio>
              </el-radio-group>
            </el-form-item>
            <el-form-item
              label="Phòng ban"
              prop="department"
            >
              <el-select
                v-model="userInfo.department"
                style="width: 100%"
                filterable
                placeholder="Vui lòng chọn"
              >
                <el-option
                  v-for="department in departmentList"
                  :key="department.id"
                  :label="department.name"
                  :value="department.id"
                />
              </el-select>
            </el-form-item>
          </div>
          <div class="item-right">
            <el-form-item

              label="Số điện thoại"
              prop="phoneNumber"
            >
              <el-input
                v-model="userInfo.phoneNumber"
                placeholder="Nhập số điện thoại"
              />
            </el-form-item>

            <el-form-item

              label="Email"
              prop="email"
            >
              <el-input v-model="userInfo.email" placeholder="Nhập email" />
            </el-form-item>

            <el-form-item label="Quyền" prop="role">
              <el-select
                v-model="userInfo.role"
                filterable
                placeholder="Chọn quyền"
                style="width: 100%"
              >
                <el-option
                  v-for="role in rolesLst"
                  :key="role.value"
                  :label="role.label"
                  :value="role.value"
                />
              </el-select>
            </el-form-item>
            <el-form-item

              label="Vị trí"
              prop="position"
            >
              <el-input
                v-model="userInfo.position"
                placeholder="Java,C#,C++..."
              />
            </el-form-item>
          </div>
        </div>
      </el-form>

      <div slot="footer">
        <el-button
          class="cancel-btn"
          type="info"
          @click="dialogEdit = false"
        >Hủy</el-button>
        <el-button
          type="primary"
          :loading="loading_add"
          @click="editUser()"
        >Lưu
        </el-button>
      </div>
    </el-dialog>

    <image-cropper
      v-show="imagecropperShow"
      :key="imagecropperKey"
      title="Cập nhật ảnh đại diện"
      :file="file"
      :width="avatarWith"
      :height="avatarHeight"
      field="file"
      url="user/avatar"
      lang-type="vi"
      @close="imagecropperShow = false"
      @crop-upload-success="cropSuccess"
    />
  </div>
</template>

<script>
import axios from 'axios'
import moment from 'moment'
import Cookies from 'js-cookie'
import user_default from '@/assets/images/user_default.png'
import { validEmail, validPhone } from '@/utils/validate'
import { inject } from 'vue'
import ImageCropper from '@/components/extra/ImageCropper'

export default {
  name: 'Accounts',
  components: {
    ImageCropper
  },
  setup() {
    const appName = inject('appName')
    console.log(appName)
  },
  filters: {
    formatDatetime: function(value) {
      if (!value) return ''
      return moment(value, 'YYYY-MM-DD').format('DD/MM/YYYY')
    }
  },
  data() {
    const validateEmail = (rule, value, callback) => {
      if (!validEmail(value)) {
        callback(new Error('Vui lòng nhập đúng định dạng email'))
      } else {
        callback()
      }
    }

    const validateMobile = (rule, value, callback) => {
      if (value && !validPhone(value)) {
        callback(new Error('Vui lòng nhập đúng định dạng số điện thoại'))
      } else {
        callback()
      }
    }

    const validateRole = (rule, value, callback) => {
      if (value == null) {
        callback(new Error('Nhóm quyền là bắt buộc'))
      } else {
        callback()
      }
    }

    const validateDepartment = (rule, value, callback) => {
      if (value == null) {
        callback(new Error('Phòng ban là bắt buộc'))
      } else {
        callback()
      }
    }

    var validatePass2 = (rule, value, callback) => {
      if (value === '') {
        callback(new Error('Xác nhận mật khẩu là bắt buộc'))
      } else if (value !== this.userInfo.password) {
        callback(new Error('Mật khẩu không trùng khớp'))
      } else {
        callback()
      }
    }

    var validatePassEdit = (rule, value, callback) => {
      if (value !== this.userInfo.password && this.userInfo.password) {
        callback(new Error('Mật khẩu không trùng khớp'))
      } else {
        callback()
      }
    }

    var validatePassEdit2 = (rule, value, callback) => {
      if (!value && this.userInfo.matchingPassword) {
        callback(new Error('Mật khẩu không được để trống'))
      } else {
        callback()
      }
    }

    return {
      loading: true,
      loadingVehicle: false,
      userList: [],
      multiSelected: [],
      allSelected: false,
      loading_delete_all: false,
      loading_add: false,
      dialogEdit: false,
      dialogAdd: false,
      queryPage: {
        page: 0,
        size: 10,
        total: 0,
        search: ''
      },
      user: {
        uuid: '',
        username: '',
        password: '',
        matchingPassword: '',
        fullName: '',
        phoneNumber: '',
        email: '',
        gender: '',
        birthday: '',
        address: '',
        avatar: '',
        role: '',
        postion: '',
        department: ''
      },
      userInfo: {
        uuid: '',
        username: '',
        password: '',
        matchingPassword: '',
        fullName: '',
        phoneNumber: '',
        email: '',
        gender: 0,
        birthday: '',
        address: '',
        role: '',
        postion: '',
        department: ''
      },
      userUpdate: {
        uuid: '',
        username: '',
        password: '',
        matchingPassword: '',
        fullName: '',
        phoneNumber: '',
        email: '',
        gender: '',
        birthday: '',
        address: '',
        avatar: '',
        role: '',
        postion: '',
        department: ''
      },
      departmentList: [],
      userDetail: {},
      imagecropperKey: 0,
      imagecropperShow: false,
      url: null,
      user_default: user_default,
      uploadReady: true,
      datePickerOptions: {
        disabledDate(date) {
          return date > moment().valueOf()
        }
      },
      file: {
        data: null
      },
      avatarHeight: 200,
      avatarWith: 200,
      rolesLst: [
        {
          value: 1,
          label: 'Quản trị viên'
        },
        {
          value: 2,
          label: 'Nhân viên'
        }
      ],
      rules: {
        fullName: [
          {
            required: true,
            message: 'Họ và tên là bắt buộc',
            trigger: 'blur'
          }
          //   { validator: vehicleType }
        ],
        username: [
          {
            required: true,
            message: 'Tên đăng nhập là bắt buộc',
            trigger: 'blur'
          },
          { max: 50, message: 'Tối đa 50 ký tự', trigger: 'blur' }
        ],
        password: [
          {
            required: true,
            message: 'Mật khẩu là bắt buộc',
            trigger: 'blur'
          },
          { min: 4, message: 'Tối thiểu 4 ký tự', trigger: 'blur' },
          { max: 50, message: 'Tối đa 50 ký tự', trigger: 'blur' }
        ],
        matchingPassword: [
          {
            required: true,
            message: 'Nhập lại mật khẩu là bắt buộc',
            trigger: 'blur'
          },
          { validator: validatePass2, trigger: 'blur' }
        ],
        birthday: [
          {
            required: true,
            message: 'Ngày sinh là bắt buộc',
            trigger: 'blur'
          }
        ],
        phoneNumber: [
          {
            required: true,
            message: 'Số điện thoại là bắt buộc',
            trigger: 'blur'
          },
          { validator: validateMobile }
        ],
        email: [
          {
            required: true,
            message: 'Email là bắt buộc',
            trigger: 'blur'
          },
          { validator: validateEmail }
        ],
        role: [
          {
            required: true,
            message: 'Quyền là bắt buộc',
            trigger: 'blur'
          },
          { validator: validateRole }
        ],
        department: [
          {
            required: true,
            message: 'Phòng ban là bắt buộc',
            trigger: 'blur'
          },
          { validator: validateDepartment }
        ]
      },
      rulesEdit: {
        fullName: [
          {
            required: true,
            message: 'Họ và tên là bắt buộc',
            trigger: 'blur'
          }
        ],
        password: [
          { min: 4, message: 'Mật khẩu tối thiểu 4 ký tự', trigger: 'blur' },
          { validator: validatePassEdit2, trigger: 'blur' }
        ],
        matchingPassword: [{ validator: validatePassEdit, trigger: 'blur' }],
        birthday: [
          {
            required: true,
            message: 'Ngày sinh là bắt buộc',
            trigger: 'blur'
          }
        ],
        phoneNumber: [
          {
            required: true,
            message: 'Số điện thoại là bắt buộc',
            trigger: 'blur'
          },
          { validator: validateMobile }
        ],
        email: [
          {
            required: true,
            message: 'Email là bắt buộc',
            trigger: 'blur'
          },
          { validator: validateEmail }
        ],
        role: [
          {
            required: true,
            message: 'Quyền là bắt buộc',
            trigger: 'blur'
          },
          { validator: validateRole }
        ],
        department: [
          {
            required: true,
            message: 'Phòng ban là bắt buộc',
            trigger: 'blur'
          },
          { validator: validateDepartment }
        ]
      }
    }
  },
  watch: {
    multiSelected(newValue) {
      if (newValue.length === 0) {
        this.indeterminate = false
        this.allSelected = false
      } else if (newValue.length === this.userList.length) {
        this.indeterminate = false
        this.allSelected = true
      } else {
        this.indeterminate = true
        this.allSelected = false
      }
    }
  },
  created() {
    this.getUser()
    this.getDepartment()
  },
  methods: {
    closeDialog(formName) {
      this.$refs[formName].clearValidate()
      this.$refs[formName].resetFields()
    },

    resetDialog() {
      this.url = null
      this.userInfo = {
        uuid: '',
        email: '',
        phoneNumber: '',
        username: '',
        fullName: '',
        matchingPassword: '',
        password: '',
        gender: '',
        birthday: '',
        address: '',
        avatar: '',
        postion: '',
        role: ''
      }
    },

    toggleAll(checked) {
      this.multiSelected = checked ? this.userList.slice() : []
    },
    handleSizeChange(size) {
      this.queryPage.size = size
      this.getUser()
    },
    handleCurrentChange(page) {
      this.queryPage.page = page
      this.getUser()
    },
    onChangeInputSearch() {
      this.queryPage.page = 0
      this.getUser()
    },
    handleSelectionChange(val) {
      this.multiSelected = val
    },
    getUser() {
      this.loading = true
      const headers = {
        'Content-Type': 'multipart/form-data',
        Authorization: 'Bearer ' + Cookies.get('access-token')
      }
      axios
        .get(process.env.VUE_APP_API + 'user', {
          headers: headers,
          params: {
            page: this.queryPage.page > 0 ? this.queryPage.page - 1 : 0,
            size: this.queryPage.size,
            search: this.queryPage.search
          }
        })
        .then((res) => {
          if (res.data) {
            this.userList = res.data.data
            this.queryPage.total = res.data.total
            this.loading = false
          }
        })
        .catch((err) => {
          console.log(err)
          this.loading = false
          this.$message({
            message: err.response.data.message,
            type: 'error'
          })
        })
    },

    getDepartment() {
      this.loading = true
      const headers = {
        'Content-Type': 'multipart/form-data',
        Authorization: 'Bearer ' + Cookies.get('access-token')
      }
      axios
        .get(process.env.VUE_APP_API + 'user/department', {
          headers: headers,
          params: {
            page: 0,
            size: 100
          }
        })
        .then((res) => {
          if (res.data) {
            this.departmentList = res.data.data
            this.queryPage.total = res.data.total
            this.loading = false
          }
        })
        .catch((err) => {
          console.log(err)
          this.loading = false
          this.$message({
            message: err.response.data.message,
            type: 'error'
          })
        })
    },

    closeDelete() {
      this.$refs.multipleTable.clearSelection()
      this.multipleSelection = []
    },

    destroyMulti() {
      this.$confirm('Bạn có chắc chắn muốn xóa lựa chọn này?', '', {
        confirmButtonText: 'Xác nhận',
        cancelButtonText: 'Thoát',
        type: 'warning'
      })
        .then(() => {
          const headers = {
            'Content-Type': 'application/json',
            Authorization: 'Bearer ' + Cookies.get('access-token')
          }
          this.loading_delete_all = true
          let params = this.multiSelected.map(function(val) {
            return val.uuid
          })
          params = {
            uuids: params
          }
          axios
            .delete(process.env.VUE_APP_API + 'user', {
              headers: headers,
              data: params
            })
            .then((res) => {
              this.multiSelected = []
              this.onChangeInputSearch()
              this.loading_delete_all = false
              this.$message({
                type: 'success',
                message: res.data.message
              })
            })
            .catch((err) => {
              console.log(err)
              this.multiSelected = []
              this.onChangeInputSearch()
              this.loading_delete_all = false

              this.$message({
                message: err.response.data.message,
                type: 'error'
              })
            })
        })
        .catch(() => {})
    },

    async rowClicked(row) {
      this.flag_active = row.uuid
      if (row) {
        this.userDetail = row
        if (this.userDetail) {
          document.getElementById('vehicle-table').style.width =
            'calc(100% - 406px)'
          setTimeout(function() {
            document.getElementById('detail-vehicle').style.display = 'block'
            document.getElementById('detail-vehicle').style.width = '380px'
          }, 500)
        }
      } else {
        this.$message({
          message: 'Không tìm thấy phương tiện',
          type: 'error'
        })
        this.onChangeInputSearch()
      }
    },

    closeDetail() {
      document.getElementById('detail-vehicle').style.display = 'none'
      document.getElementById('detail-vehicle').style.width = '0'
      document.getElementById('vehicle-table').style.width = 'calc(100%)'
    },

    destroy(vehicle) {
      this.$confirm('Bạn có chắc chắn muốn xóa lựa chọn này?', '', {
        confirmButtonText: 'Xác nhận',
        cancelButtonText: 'Thoát',
        type: 'warning'
      })
        .then(() => {
          const headers = {
            'Content-Type': 'application/json',
            Authorization: 'Bearer ' + Cookies.get('access-token')
          }
          this.loadingVehicle = true
          let params = this.multiSelected.map(function(val) {
            return val.uuid
          })
          params.push(vehicle.uuid)
          params = {
            uuids: params
          }
          axios
            .delete(process.env.VUE_APP_API + 'user', {
              headers: headers,
              data: params
            })
            .then((res) => {
              console.log(res)
              this.multiSelected = []
              this.onChangeInputSearch()
              this.closeDetail()
              this.loadingVehicle = false
              this.$message({
                type: 'success',
                message: res.data.message
              })
            })
            .catch((err) => {
              console.log(err)
              this.multiSelected = []
              this.onChangeInputSearch()
              this.loadingVehicle = false

              this.$message({
                message: err.response.data.message,
                type: 'error'
              })
            })
        })
        .catch(() => {})
    },

    handleCreate() {
      this.resetDialog()
      this.dialogAdd = true
      this.userInfo.gender = 0
    },
    handleEdit(data) {
      this.userInfo = _.cloneDeep(data)
      this.url = data.avatar
      this.dialogEdit = true
      this.$nextTick(() => {
        this.$refs['editForm'].clearValidate()
      })
    },

    editUser() {
      this.userInfo = this.$root.trimData(this.userInfo)
      this.$refs.editForm.validate((valid) => {
        if (valid) {
          const params = {
            username: this.userInfo.username.trim(),
            fullName: this.userInfo.fullName.trim(),
            phoneNumber: this.userInfo.phoneNumber.trim(),
            email: this.userInfo.email.trim(),
            gender: this.userInfo.gender,
            birthday: this.userInfo.birthday,
            address: this.userInfo.address.trim(),
            role: this.userInfo.role,
            department: this.userInfo.department.id,
            password: this.userInfo.password,
            matchingPassword: this.userInfo.matchingPassword,
            position: this.userInfo.position
          }
          const headers = {
            'Content-Type': 'application/json',
            Authorization: 'Bearer ' + Cookies.get('access-token')
          }
          this.loadingVehicle = true
          params.avatar = this.url
          params.birthday = moment(params.birthday).format('DD/MM/YYYY')
          axios
            .put(
              process.env.VUE_APP_API + 'user/' + this.userInfo.uuid,
              params,
              { headers }
            )
            .then((response) => {
              if (
                response.data.status === 200 ||
                response.data.status === 201
              ) {
                this.dialogEdit = false
                this.closeDetail()
                this.getUser()
                this.$message({
                  message: response.data.message,
                  type: 'success'
                })
                this.resetDialog()
              } else {
                this.dialogEdit = false
                this.getUser()
                this.$message({
                  message: response.data.message,
                  type: 'error'
                })
              }
              this.loadingVehicle = false
              this.resetDialog()
            })
            .catch((err) => {
              this.loadingVehicle = false
              this.$message({
                message: err.response.data.message,
                type: 'error'
              })
            })
        } else {
          return false
        }
      })
    },
    resetUploadFile() {
      this.uploadReady = false
      this.$nextTick(() => {
        this.uploadReady = true
      })
    },

    cropSuccess(data) {
      if (data.data[0].fileDownloadUri) this.url = data.data[0].fileDownloadUri
    },
    fileSelected(e) {
      const file = e.target.files[0]
      if (file) {
        this.checkFile(file)
          .then(() => {
            this.file.data = file
            this.imagecropperShow = true
            this.resetUploadFile()
          })
          .catch((err) => {
            this.$message({
              message: err,
              type: 'error',
              duration: 5 * 1000,
              showClose: true
            })
          })
      }
    },
    checkFile(file) {
      return new Promise((resolve, reject) => {
        const self = this
        if (file.type.indexOf('image') === -1) {
          return reject('Định dạng không cho phép')
        }
        if (file.size > 5242880) {
          return reject('Vượt quá dung lượng cho phép tối đa 5Mb')
        }
        const reader = new FileReader()
        reader.readAsDataURL(file)
        reader.onload = function(e) {
          const image = new Image()
          image.src = e.target.result
          image.onload = function() {
            const height = this.height
            const width = this.width
            if (height < self.avatarHeight || width < self.avatarWith) {
              return reject(
                'Kích thước hình ảnh quá nhỏ. Tối thiểu là: ' +
                  self.avatarWith +
                  '*' +
                  self.avatarHeight
              )
            }
            return resolve()
          }
        }
      })
    },
    closeChangeAvatar() {},
    handleClick(e) {
      if (e.target !== this.$refs.file) {
        e.preventDefault()
        if (document.activeElement !== this.$refs) {
          this.$refs.file.click()
        }
      }
    },

    addUser() {
      this.userInfo = this.$root.trimData(this.userInfo)
      this.$refs.addForm.validate((valid) => {
        if (valid) {
          const headers = {
            'Content-Type': 'application/json',
            Authorization: 'Bearer ' + Cookies.get('access-token')
          }
          const data = _.cloneDeep(this.userInfo)
          // const paramRegister = this.userInfo
          const paramRegister = {
            username: data.username,
            password: data.password,
            matchingPassword: data.matchingPassword,
            email: data.email,
            phoneNumber: data.phoneNumber,
            fullName: data.fullName,
            gender: data.gender,
            birthday: data.birthday,
            address: data.address,
            role: data.role,
            department: data.department.id,
            position: data.position
          }
          paramRegister.avatar = this.url
          paramRegister.birthday = moment(paramRegister.birthday).format(
            'DD/MM/YYYY'
          )
          this.loading_add = true
          axios
            .post(process.env.VUE_APP_API + 'user', paramRegister, { headers })
            .then((response) => {
              console.log(response.data)
              if (
                response.data.status === 200 ||
                response.data.status === 201
              ) {
                this.$message({
                  type: 'success',
                  message: response.data.message
                })
                this.dialogAdd = false
                this.getUser()
                this.loading_add = false
                this.resetDialog()
              } else {
                this.$message({
                  message: response.data.message,
                  type: 'error'
                })
                this.dialogAdd = false
                this.loading_add = false
                this.getUser()
                this.resetDialog()
              }
            })
            .catch((err) => {
              this.loading_add = false
              this.$message({
                message: err.response.data.message,
                type: 'error'
              })
            })
        } else {
          return false
        }
      })
    }
  }
}
</script>

<style lang="scss">
.el-dialog {
  margin-top: 30px !important;
}

.item-left {
  width: 48%;
  float: left;
  margin-right: 2%;
}

.item-right {
  width: 48%;
  float: left;
  margin-left: 2%;
}

.block-item {
  width: 100%;
  float: left;
}

#select-all-delete .el-icon-close {
  font-size: 14px;
  margin-left: 18px;
  margin-right: 29px;
  top: 4px;
  cursor: pointer;
}

.el-icon-camera-solid {
  font-size: 25px;
  color: #9a9a9a;
  position: relative;
  top: -50px;
  left: 54px;
  background: #dfdfdf;
  border-radius: 50%;
  padding: 8px;
  border: 1px solid white;
}
</style>
