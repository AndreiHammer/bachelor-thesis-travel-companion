package eu.ase.travelcompanionapp.user.presentation.profile

import android.widget.Toast
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import eu.ase.travelcompanionapp.R
import eu.ase.travelcompanionapp.user.presentation.profile.components.AccountActionsCard
import eu.ase.travelcompanionapp.user.presentation.profile.components.PersonalInfoCard
import eu.ase.travelcompanionapp.user.presentation.profile.components.ProfileHeader
import org.koin.androidx.compose.koinViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(
    viewModel: ProfileViewModel = koinViewModel()
) {
    val userData by viewModel.userData.collectAsStateWithLifecycle()
    val actionState by viewModel.actionState.collectAsStateWithLifecycle()
    val showSignOutDialog by viewModel.showSignOutDialog.collectAsStateWithLifecycle()
    val showDeleteDialog by viewModel.showDeleteDialog.collectAsStateWithLifecycle()
    val context = LocalContext.current

    var name by remember(userData) { mutableStateOf(userData.name) }
    var phoneNumber by remember(userData) { mutableStateOf(userData.phoneNumber) }
    var birthDate by remember(userData) { mutableStateOf(userData.birthDate) }
    var gender by remember(userData) { mutableStateOf(userData.gender) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.profile)) },
                navigationIcon = {
                    IconButton(onClick = { viewModel.handleAction(ProfileAction.OnBackClick) }) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = stringResource(R.string.back)
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    titleContentColor = MaterialTheme.colorScheme.onPrimary
                ),
                actions = {
                    IconButton(
                        onClick = { viewModel.handleAction(ProfileAction.OnSettingsClick) }
                    ) {
                        Icon(
                            imageVector = Icons.Filled.Settings,
                            contentDescription = stringResource(R.string.settings)
                        )
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            ProfileHeader(userData)

            PersonalInfoCard(
                name = name,
                email = userData.email,
                phoneNumber = phoneNumber,
                birthDate = birthDate,
                gender = gender,
                onNameChange = { name = it },
                onPhoneChange = { phoneNumber = it },
                onBirthDateChange = { birthDate = it },
                onGenderChange = { gender = it },
                onSaveClick = {
                    viewModel.updateUserData(
                        userData.copy(
                            name = name,
                            phoneNumber = phoneNumber,
                            birthDate = birthDate,
                            gender = gender
                        )
                    )
                }
            )

            AccountActionsCard(
                onSignOutClick = { viewModel.setShowSignOutDialog(true) },
                onDeleteAccountClick = { viewModel.setShowDeleteDialog(true) }
            )
        }
    }

    // Dialogs
    if (showSignOutDialog) {
        AlertDialog(
            onDismissRequest = { viewModel.setShowSignOutDialog(false) },
            title = { Text(stringResource(R.string.sign_out)) },
            text = { Text(stringResource(R.string.are_you_sure_you_want_to_sign_out)) },
            confirmButton = {
                Button(
                    onClick = {
                        viewModel.setShowSignOutDialog(false)
                        viewModel.signOut()
                    }
                ) {
                    Text(stringResource(R.string.sign_out))
                }
            },
            dismissButton = {
                TextButton(onClick = { viewModel.setShowSignOutDialog(false) }) {
                    Text(stringResource(R.string.cancel))
                }
            }
        )
    }

    if (showDeleteDialog) {
        AlertDialog(
            onDismissRequest = { viewModel.setShowDeleteDialog(false) },
            title = { Text(stringResource(R.string.delete_account)) },
            text = { Text(stringResource(R.string.are_you_sure_you_want_to_delete_your_account_this_action_cannot_be_undone)) },
            confirmButton = {
                Button(
                    onClick = {
                        viewModel.setShowDeleteDialog(false)
                        viewModel.deleteAccount()
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.error
                    )
                ) {
                    Text(stringResource(R.string.delete))
                }
            },
            dismissButton = {
                TextButton(onClick = { viewModel.setShowDeleteDialog(false) }) {
                    Text(stringResource(R.string.cancel))
                }
            }
        )
    }

    LaunchedEffect(actionState) {
        when (actionState) {
            is ProfileActionState.SignedOut -> viewModel.handleAction(ProfileAction.SignedOut)
            is ProfileActionState.AccountDeleted -> viewModel.handleAction(ProfileAction.AccountDeleted)
            is ProfileActionState.Error -> {
                Toast.makeText(context, (actionState as ProfileActionState.Error).message, Toast.LENGTH_SHORT).show()
            }
            is ProfileActionState.Success -> {
                Toast.makeText(context,
                    context.getString(R.string.profile_updated_successfully), Toast.LENGTH_SHORT).show()
            }
            else -> Unit
        }
    }
}
