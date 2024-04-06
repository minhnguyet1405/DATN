<template>
  <el-card style="width: 550px; margin-top: 20px">
    <div class="profile-editor-container">
      <div class="chart-wrapper update-password-block">
        <div class="avatar">
          <div class="header">
            <h2>Trang cá nhân</h2>
          </div>
          <div class="body">
            <!-- <img v-if="user.avatar" :src="user.avatar" alt="avatar">
          <img v-else :src="imgUserDefault" alt="avatar default"> -->

            <pan-thumb v-if="userInfo.avatar" :image="userInfo.avatar" :height="'100px'" :width="'100px'" :hoverable="false">
              {{ userInfo.role === 1 ? 'Quản trị viên' : 'Nhân viên' }}
            </pan-thumb>
            <pan-thumb v-else :image="user.avatar" :height="'100px'" :width="'100px'" :hoverable="false">
              {{ userInfo.role === 1 ? 'Quản trị viên' : 'Nhân viên' }}
            </pan-thumb>
          </div>
        </div>
        <div class="general-info">
          <div class="body">
            <p v-if="userInfo.fullName"><span class="label">Họ và tên </span> <span class="value">{{ user && userInfo.fullName }}</span></p>
            <p v-if="userInfo.username"><span class="label">Tài khoản </span> <span class="value">{{ user && userInfo.username }}</span></p>
            <p v-if="userInfo.email"><span class="label">Email </span> <span class="value">{{ user && userInfo.email }}</span></p>
            <p v-if="userInfo.phoneNumber"><span class="label">Số điện thoại </span> <span class="value">{{ user && userInfo.phoneNumber }}</span></p>
          </div>
        </div>
        <div class="action">
          <el-button
            type="primary"
            @click="handleEdit(userInfo)"
          > Cập nhật thông tin
          </el-button>
          <el-button
            class="change-password"
            type="primary"
            @click="handleUpdatePassword()"
          > Đổi mật khẩu
          </el-button>
        </div>
      </div>

      <el-dialog
        class="update-password"
        title="Đổi mật khẩu"
        :visible.sync="dialogUpdate"
        width="450px"
        :close-on-click-modal="false"
        @close="closeDialog('updateForm')"
      >
        <el-form
          ref="updateForm"
          :model="userPass"
          :rules="rulesUpdatePass"
          label-position="top"
          label-width="100%"
        >
          <div class="block-item">
            <el-form-item label="Mật khẩu hiện tại" prop="currentPassword">
              <el-input v-model="userPass.currentPassword" maxlength="32" type="password" />
            </el-form-item>

            <el-form-item label="Mật khẩu mới" prop="newPassword">
              <el-input v-model="userPass.newPassword" maxlength="32" type="password" />
            </el-form-item>

            <el-form-item label="Xác nhận mật khẩu" prop="matchingPassword">
              <el-input
                v-model="userPass.matchingPassword"
                type="password"
                maxlength="32"
              />
            </el-form-item>
          </div>
        </el-form>
        <div slot="footer" class="dialog-footer">
          <el-button
            class="cancel-btn"
            type="info"
            @click="dialogUpdate = false"
          >Hủy</el-button>
          <el-button
            type="primary"
            :loading="loading_add"
            @click="updatePassword()"
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
                style="margin-bottom: 21px"
                label="Họ và tên"
                prop="fullName"
              >
                <el-input
                  v-model="userInfo.fullName"
                  placeholder="Nhập họ và tên"
                />
              </el-form-item>

              <el-form-item
                style="margin-bottom: 21px"
                label="Số điện thoại"
                prop="phoneNumber"
              >
                <el-input
                  v-model="userInfo.phoneNumber"
                  placeholder="Nhập số điện thoại"
                />
              </el-form-item>

              <el-form-item
                style="margin-bottom: 21px"
                label="Email"
                prop="email"
              >
                <el-input v-model="userInfo.email" placeholder="Nhập email" />
              </el-form-item>
              <el-form-item
                style="margin-bottom: 21px"
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
                style="margin-bottom: 21px"
                label="Giới tính"
                prop="gender"
              >
                <el-radio-group v-model="userInfo.gender">
                  <el-radio :label="0">Nam</el-radio>
                  <el-radio :label="1">Nữ</el-radio>
                </el-radio-group>
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
                  <img v-else class="image" @click="handleClick">
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
        </el-form>

        <div slot="footer" class="dialog-footer">
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
  </el-card>
</template>

<script>
import PanThumb from '@/components/PanThumb'
import Cookies from 'js-cookie'
import axios from 'axios'
import moment from 'moment'
import ImageCropper from '@/components/extra/ImageCropper'
import user_default from '@/assets/images/user_default.png'
import { validEmail, validPhone } from '@/utils/validate'
import { removeToken } from '@/utils/auth'

export default {
  components: { PanThumb, ImageCropper },
  props: {
    user: {
      type: Object,
      default: () => {
        return {
          name: '',
          email: '',
          avatar: '',
          role: ''
        }
      }
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

    var validatePass2 = (rule, value, callback) => {
      if (value === '') {
        callback(new Error('Xác nhận mật khẩu là bắt buộc'))
      } else if (value !== this.userInfo.newPassword) {
        callback(new Error('Mật khẩu không trùng khớp'))
      } else {
        callback()
      }
    }

    return {
      userPass: {
        currentPassword: null,
        newPassword: null,
        matchingPassword: null
      },
      userInfo: null,
	    uuid: '',
	    dialogEdit: false,
	    dialogUpdate: false,
	    loading_add: false,
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
      rulesEdit: {
        fullName: [
          {
            required: true,
            message: 'Họ và tên là bắt buộc',
            trigger: 'blur'
          }
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
        ]
      },
	  rulesUpdatePass: {
        currentPassword: [
          {
            required: true,
            message: 'Mật khẩu hiện tại là bắt buộc',
            trigger: 'blur'
          }
        ],
        newPassword: [
          {
            required: true,
            message: 'Mật khẩu mới là bắt buộc',
            trigger: 'blur'
          },
          { min: 4, message: 'Mật khẩu tối thiểu 4 ký tự', trigger: 'blur' }
        ],
        matchingPassword: [
          {
            required: true,
            message: 'Nhập lại mật khẩu là bắt buộc',
            trigger: 'blur'
          },
		  { validator: validatePass2, trigger: 'blur' }
        ]
	  }
    }
  },
  created() {
    this.getUser()
  },
  methods: {
    closeDialog(formName) {
      this.$refs[formName].clearValidate()
      this.$refs[formName].resetFields()
    },
    getUser() {
      const headers = {
        'Content-Type': 'multipart/form-data',
        Authorization: 'Bearer ' + Cookies.get('access-token')
      }
      axios
        .get(process.env.VUE_APP_API + 'user/' + Cookies.getJSON('userInfo').uuid, {
          headers: headers
        })
        .then((res) => {
          if (res.data) {
            this.userInfo = res.data.data
            this.userInfo.uuid = Cookies.getJSON('userInfo').uuid
          }
        })
        .catch((err) => {
          console.log(err)
          this.$message({
            message: err.response.data.message,
            type: 'error'
          })
        })
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
        role: ''
      }
    },

    handleEdit(data) {
      this.userInfo = _.cloneDeep(data)
	  this.url = this.userInfo.avatar
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
            role: this.userInfo.role
          }
          const headers = {
            'Content-Type': 'application/json',
            Authorization: 'Bearer ' + Cookies.get('access-token')
          }
		  params.avatar = this.url
		  params.birthday = moment(params.birthday).format('DD/MM/YYYY')
          axios
            .put(process.env.VUE_APP_API + 'user/' + this.userInfo.uuid, params, { headers })
            .then((response) => {
              if (
                response.data.status === 200 ||
                response.data.status === 201
              ) {
                this.dialogEdit = false
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
              this.resetDialog()
            })
            .catch((err) => {
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
    handleUpdatePassword() {
      // this.resetDialog()
      this.dialogUpdate = true
    },

    async logout() {
      await this.$store.dispatch('user/logout')
      this.$router.push(`/login?redirect=${this.$route.fullPath}`)
      // eslint-disable-next-line no-mixed-spaces-and-tabs
      removeToken()
      Cookies.remove('access-token')
      Cookies.remove('userInfo')
    },

    async updatePassword() {
      this.$refs.updateForm.validate((valid) => {
    	if (valid) {
          const headers = {
            'Content-Type': 'application/json',
            Authorization: 'Bearer ' + Cookies.get('access-token')
          }
          const params = {
            currentPassword: this.userPass.currentPassword,
            newPassword: this.userPass.newPassword,
            matchingPassword: this.userPass.matchingPassword
          }
		  this.loading_add = true
          axios
            .put(process.env.VUE_APP_API + 'user/password', params, {
              headers: headers
            })
            .then((res) => {
              if (res.data) {
                this.loading_add = false
                this.dialogUpdate = false
                this.$message({
                  message: res.data.message,
                  type: 'success'
                })
                this.logout()
              }
            })
            .catch((err) => {
              this.loading_add = false
              console.log(err.response.data.message)
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

<style rel="stylesheet/scss" lang="scss" scope>

.dialog-footer {
	.cancel-btn {
		float: left;
	}

}

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

.action {
	text-align: center;
	.change-password {
		background-color: #0051a7;
		border-color: #0051a7;
	}
	.change-password:hover {
		background-color: #0070e8;
		border-color: #0070e8;
	}
}
.app-container {
	display: flex;
    justify-content: center;
    align-items: center;
}
.profile-editor-container {
  .chart-wrapper {
    background: #FFFFFF;
    border-radius: 2px;
    h2{
      font-size:18px;
      line-height: 26px;
      color:#212633;
      text-align: center;
      margin: 16px 0px;
    }
    .avatar {
      text-align: center;
      .body {
        img {
          width: 128px;
          margin: auto;
          border-radius: 100%;
        }
      }
    }
    .general-info {
      width: 300px;
      margin: 0 auto 28px;
      .body{
		margin: 0px !important;
        span{
          font-size: 14px;
          color: #212633;
          line-height: 20px;
        }
        .label {
          color: #525B73;
          font-weight: bold;
          min-width: 100px;
          display: inline-block;
        }
      }
    }
    .btn-center {
      margin: auto;
    }
  }
//   .el-dialog__header {
//     padding: 0;
//     background: #006fe3;
//     height: 40px;
//     line-height: 40px;
//     text-align: center;
//     .el-dialog__title {
//       color: white;
//     }
//     .el-icon-close {
//       color: white;
//       font-size: 22px;
//       top: -10px;
//       position: relative;
//     }
//   }
//   .el-dialog__body {
//     .el-form-item__label {
//       padding-bottom: 0;
//       font-size: 14px;
//       color: #363030;
//       font-weight: 500 !important;
//     }
//   }
//   .el-dialog__footer {
//     clear: both;
//     padding-bottom: 25px;
//     .dialog-footer {
//       display: flex;
//       justify-content: center;
//       .btn-cancel {
//         height: 32px;
//         padding: 0 20px;
//         width: 97px;
//         background: transparent;
//         color: #7C7E81;
//         border: 2px solid #7C7E81;
//       }
//       .btn-cancel:hover {
//         background: transparent;
//         color: #7C7E81;
//         border: 2px solid #7C7E81;
//       }
//     }
//   }
}
</style>
